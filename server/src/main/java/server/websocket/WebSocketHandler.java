package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private static final ConnectionManager connections = new ConnectionManager();


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
                throw new Exception("Error: Game does not exist");
            }

            connections.addConnection(command.getGameID(), username, context);
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

            connections.broadcast(command.getGameID(), context, notification);

        } catch (Exception e) {
            System.out.println("DEBUG: CONNECT Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void makeMove(WsMessageContext ctx, String json) { /* TODO: logic later */}
    private void leave(WsMessageContext ctx, String json) { /* TODO: logic later */ }
    private void resign(WsMessageContext ctx, String json) { /* TODO: logic later */ }
}
