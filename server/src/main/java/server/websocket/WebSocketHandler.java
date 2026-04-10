package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.HashSet;
import java.util.Set;


public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private static final ConnectionManager CONNECTIONS = new ConnectionManager();
    private final Set<Integer> resignedGames = new HashSet<>();


    public WebSocketHandler(WsConfig ws, AuthDAO authDAO, GameDAO gameDAO) {
        ws.onMessage(this::onMessage);
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    private void onMessage(WsMessageContext ctx){
        String json = ctx.message();

        UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> connect(ctx, json);
            case MAKE_MOVE -> makeMove(ctx, json);
            case LEAVE -> leave(ctx, json);
            case RESIGN -> resign(ctx, json);
        }
    }

    private void connect(WsMessageContext context, String json) {
        try {
            UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);
            System.out.println("CONNECT received for GameID: " + command.getGameID());

            AuthData authData = authDAO.get(command.getAuthToken());

            if (authData == null) {
                throw new Exception("Invalid auth token");
            }

            System.out.println("User identified as: " + authData.username());

            String username = authData.username();
            GameData gameData = gameDAO.get(Integer.toString(command.getGameID()));

            if (gameData == null) {
                throw new Exception("Game does not exist");
            }

            CONNECTIONS.addConnection(command.getGameID(), username, context);
            System.out.println("Added " + username + " to ConnectionManager");

            var loadGameMessage = new LoadGameMessage(gameData.game());
            String jsonMessage = new Gson().toJson(loadGameMessage);
            context.send(jsonMessage);

            String message;
            if (username.equals(gameData.whiteUsername())) {
                message = String.format("%s joined the game as WHITE", username);
            } else if (username.equals(gameData.blackUsername())) {
                message = String.format("%s joined the game as BLACK", username);
            } else {
                message = String.format("%s joined the game as an OBSERVER", username);
            }

            var notification = new NotificationMessage(message);

            CONNECTIONS.broadcast(command.getGameID(), context, notification);

        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("Error: " + e.getMessage());
            String jsonError = new Gson().toJson(error);
            context.send(jsonError);
            System.out.println("Sent error to client: " + jsonError);
        }
    }

    private void makeMove(WsMessageContext ctx, String json) {
        try {
            MakeMoveCommand command = new Gson().fromJson(json, MakeMoveCommand.class);
            String username = authDAO.get(command.getAuthToken()).username();
            GameData gameData = gameDAO.get(Integer.toString(command.getGameID()));
            ChessGame game = gameData.game();

            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) { playerColor = ChessGame.TeamColor.WHITE; }
            if (username.equals(gameData.blackUsername())) { playerColor = ChessGame.TeamColor.BLACK; }
            boolean isWhite = username.equals(gameData.whiteUsername());
            boolean isBlack = username.equals(gameData.blackUsername());

            if (!isWhite && !isBlack) { throw new Exception("Observers cannot make moves"); }
            if (game.getTeamTurn() != playerColor) { throw new Exception("It is not your turn"); }
            if (isGameOver(game) || resignedGames.contains(command.getGameID())) {
                throw new Exception("The game is over. No further moves are allowed.");
            }

            game.makeMove(command.getMove());
            gameDAO.updateGame(gameData);

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                CONNECTIONS.broadcastToAll(command.getGameID(), new NotificationMessage("Checkmate! Black wins."));
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                CONNECTIONS.broadcastToAll(command.getGameID(), new NotificationMessage("Checkmate! White wins."));
            } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                CONNECTIONS.broadcastToAll(command.getGameID(), new NotificationMessage("The game ended in a stalemate."));
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                CONNECTIONS.broadcast(command.getGameID(), ctx, new NotificationMessage("White is in check!"));
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                CONNECTIONS.broadcast(command.getGameID(), ctx, new NotificationMessage("Black is in check!"));
            }

            LoadGameMessage loadGame = new LoadGameMessage(game);
            String responseJson = new Gson().toJson(loadGame);
            ctx.send(responseJson);
            CONNECTIONS.broadcast(command.getGameID(), ctx, loadGame);

            String moveDesc = String.format("%s moved %s", username, command.getMove());
            NotificationMessage notification = new NotificationMessage(moveDesc);
            CONNECTIONS.broadcast(command.getGameID(), ctx, notification);

        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("Error: " + e.getMessage());
            ctx.send(new Gson().toJson(error));
        }
    }

    private void leave(WsMessageContext ctx, String json) {
        try {
            UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);

            String username = authDAO.get(command.getAuthToken()).username();

            GameData gameData = gameDAO.get(Integer.toString(command.getGameID()));
            if (gameData != null) {
                GameData updatedGame = removePlayerFromGame(gameData, username);
                gameDAO.updateGame(updatedGame);
            }

            CONNECTIONS.removeConnection(command.getGameID(), username);

            String message = String.format("%s has left the game", username);
            NotificationMessage notification = new NotificationMessage(message);
            CONNECTIONS.broadcast(command.getGameID(), ctx, notification);

        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private GameData removePlayerFromGame(GameData gameData, String username) {
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();

        if (username.equals(white)) {
            white = null;
        }
        else if (username.equals(black)) {
            black = null;
        }

        return new GameData(gameData.gameID(), white, black, gameData.gameName(), gameData.game());
    }

    private void resign(WsMessageContext ctx, String json) {
        try {
            UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);
            String username = authDAO.get(command.getAuthToken()).username();
            GameData gameData = gameDAO.get(Integer.toString(command.getGameID()));

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                throw new Exception("Only players can resign.");
            }

            if (resignedGames.contains(command.getGameID())) {
                throw new Exception("Cannot resign because the game is already over.");
            }

            if (gameData.game().getTeamTurn() == null) {
                throw new Exception("The game is already over.");
            }

            resignedGames.add(command.getGameID());

            String msg = username + " has resigned. The game is over.";
            CONNECTIONS.broadcastToAll(command.getGameID(), new NotificationMessage(msg));

        } catch (Exception e) {
            ctx.send(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    private boolean isGameOver(ChessGame game) {
        return game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK);
    }

}
