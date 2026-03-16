package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
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

    public Collection<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData authData = authDatabase.get(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        return gameDatabase.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData authData = authDatabase.get(authToken);
        if (gameName == null || gameName.isEmpty()) {
            throw new BadRequestException();
        }
        if (authData == null) {
            throw new UnauthorizedException();
        }
        int gameID = new Random().nextInt(1000000); // Generate a random game ID
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDatabase.insert(newGame);
        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataAccessException, UnauthorizedException, AlreadyTakenException, BadRequestException {
        AuthData auth = authDatabase.get(authToken);
        if (auth == null) {
            throw new UnauthorizedException(); // 401
        }
        String username = auth.username();
        GameData game = gameDatabase.get(String.valueOf(gameID));
        if (game == null) {
            throw new BadRequestException();
        }

        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();

        if (playerColor == null || playerColor.isEmpty()) {
            throw new BadRequestException(); // 400
        }

        String colorUpper = playerColor.toUpperCase();
        if (colorUpper.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            whiteUser = username;
        } else if (colorUpper.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            blackUser = username;
        } else {
            // 3. This catches "GREEN" or any other invalid color
            throw new BadRequestException(); // 400
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
