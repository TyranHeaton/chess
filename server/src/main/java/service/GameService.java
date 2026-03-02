package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final GameDAO gameDatabase;
    private final AuthDAO authDatabase;

    public GameService(GameDAO gameDatabase, AuthDAO authDatabase) {
        this.gameDatabase = gameDatabase;
        this.authDatabase = authDatabase;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDatabase.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDatabase.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = authDatabase.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = new Random().nextInt(1000000); // Generate a random game ID
        GameData newGame = new GameData(gameID, gameName, null, null, new ChessGame());
        gameDatabase.insert(newGame);
        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        AuthData auth = authDatabase.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized"); // 401
        }
        String username = auth.username();
        GameData game = gameDatabase.get(String.valueOf(gameID));
        if (game == null) {
            throw new DataAccessException("Error: game not found"); // 404
        }

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            if (whiteUser != null) throw new DataAccessException("Error: already taken"); // 403
            whiteUser = username;
        } else if (playerColor.equals("BLACK")) {
            if (blackUser != null) throw new DataAccessException("Error: already taken"); // 403
            blackUser = username;
        } else {
            throw new DataAccessException("Error: bad request"); // Invalid color
        }

        GameData updatedGame = new GameData(
                game.gameID(),
                whiteUser,
                blackUser,
                game.gameName(),
                game.game()
        );
        gameDatabase.updateGame(updatedGame);
    }
}
