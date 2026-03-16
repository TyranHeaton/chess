package dataaccess;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

public class DatabaseDAOTests {
    private final UserDAO userDAO = new MySQLUserDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authTokenDAO = new MySQLAuthDAO();

    @BeforeEach
    public void setup() throws DataAccessException {
        new ClearService(new MySQLUserDAO(), new MySQLGameDAO(), new MySQLAuthDAO()).clear();
    }

    @Test
    public void insertUserPositive() throws DataAccessException{
        UserData user = new UserData("testUser", "testPassword", "test@email.com");
        Assertions.assertDoesNotThrow(() -> userDAO.insert(user));
        UserData retrieved = userDAO.get("testUser");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("testUser", retrieved.username());
    }

    @Test
    public void insertUserNegative() throws DataAccessException {
        UserData user = new UserData("testUser", "testPassword", "test@email.com");
        userDAO.insert(user);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insert(user));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "testPassword", "test@email.com");
        userDAO.insert(user);
        UserData retrieved = userDAO.get("testUser");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("testUser", retrieved.username());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData retrieved = userDAO.get("nonExistentUser");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void clearUsers() throws DataAccessException {
        UserData user1 = new UserData("testUser1", "testPassword1", "test@email1.com");
        UserData user2 = new UserData("testUser2", "testPassword2", "test@email2.com");
        userDAO.insert(user1);
        userDAO.insert(user2);
        userDAO.clear();
        Assertions.assertNull(userDAO.get("testUser1"));
        Assertions.assertNull(userDAO.get("testUser2"));
    }

    @Test
    public void deleteUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "testPassword", "test@email");
        userDAO.insert(user);
        userDAO.delete("testUser");
        Assertions.assertNull(userDAO.get("testUser"));
    }

    @Test
    public void deleteUserNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> userDAO.delete("nonExistentUser"));
    }

    @Test
    public void insertGamePositive() throws DataAccessException {
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", newGame);
        Assertions.assertDoesNotThrow(() -> gameDAO.insert(gameData));
        GameData retrieved = gameDAO.get("1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(1, retrieved.gameID());
    }

    @Test
    public void insertGameNegative() throws DataAccessException {
        GameData firstGame = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", new ChessGame());
        gameDAO.insert(firstGame);
        GameData duplicateGame = new GameData(1, "whitePlayer2", "blackPlayer2", "Test Game 2", new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.insert(duplicateGame));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", new ChessGame());
        gameDAO.insert(gameData);
        GameData retrieved = gameDAO.get("1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(1, retrieved.gameID());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData retrieved = gameDAO.get("1");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        GameData game1 = new GameData(1, "whitePlayer1", "blackPlayer1", "Test Game 1", new ChessGame());
        GameData game2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Test Game 2", new ChessGame());
        gameDAO.insert(game1);
        gameDAO.insert(game2);
        var games = gameDAO.listGames();
        Assertions.assertEquals(2, games.size());
    }

     @Test
     public void listGamesNegative() throws DataAccessException {
         var games = gameDAO.listGames();
         Assertions.assertTrue(games.isEmpty());
     }

     @Test
     public void clearGames() throws DataAccessException {
         GameData game1 = new GameData(1, "whitePlayer1", "blackPlayer1", "Test Game 1", new ChessGame());
         GameData game2 = new GameData(2, "whitePlayer2", "blackPlayer2", "Test Game 2", new ChessGame());
         gameDAO.insert(game1);
         gameDAO.insert(game2);
         gameDAO.clear();
         var games = gameDAO.listGames();
         Assertions.assertTrue(games.isEmpty());
     }

    @Test
    public void updateGamePositive() throws DataAccessException {
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", new ChessGame());
        gameDAO.insert(gameData);
        GameData updatedGame = new GameData(1, "whitePlayerUpdated", "blackPlayerUpdated", "Test Game Updated", new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(updatedGame));
        GameData retrieved = gameDAO.get("1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("whitePlayerUpdated", retrieved.whiteUsername());
        Assertions.assertEquals("blackPlayerUpdated", retrieved.blackUsername());
        Assertions.assertEquals("Test Game Updated", retrieved.gameName());
    }

     @Test
     public void updateGameNegative() throws DataAccessException {
         GameData updatedGame = new GameData(1, "whitePlayerUpdated", "blackPlayerUpdated", "Test Game Updated", new ChessGame());
         Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(updatedGame));
    }

    @Test
    public void clearAuthPositive() throws DataAccessException {
        authTokenDAO.insert(new model.AuthData("token1", "user1"));
        authTokenDAO.insert(new model.AuthData("token2", "user2"));
        authTokenDAO.clear();
        Assertions.assertNull(authTokenDAO.get("token1"));
        Assertions.assertNull(authTokenDAO.get("token2"));
    }

    @Test
    public void clearAuthNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(authTokenDAO::clear);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        authTokenDAO.insert(new model.AuthData("token1", "user1"));
        authTokenDAO.delete("token1");
        Assertions.assertNull(authTokenDAO.get("token1"));
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> authTokenDAO.delete("nonExistentToken"));
    }

    @Test
    public void insertAuthPositive() throws DataAccessException {
        model.AuthData authData = new model.AuthData("token1", "user1");
        Assertions.assertDoesNotThrow(() -> authTokenDAO.insert(authData));
        model.AuthData retrieved = authTokenDAO.get("token1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("user1", retrieved.username());
    }

    @Test
    public void insertAuthNegative() throws DataAccessException {
        model.AuthData authData = new model.AuthData("token1", "user1");
        authTokenDAO.insert(authData);
        Assertions.assertThrows(DataAccessException.class, () -> authTokenDAO.insert(authData));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        model.AuthData authData = new model.AuthData("token1", "user1");
        authTokenDAO.insert(authData);
        model.AuthData retrieved = authTokenDAO.get("token1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("user1", retrieved.username());
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        model.AuthData retrieved = authTokenDAO.get("nonExistentToken");
        Assertions.assertNull(retrieved);
    }

    @Test
    public void clearAuth() throws DataAccessException {
        model.AuthData authData1 = new model.AuthData("token1", "user1");
        model.AuthData authData2 = new model.AuthData("token2", "user2");
        authTokenDAO.insert(authData1);
        authTokenDAO.insert(authData2);
        authTokenDAO.clear();
        Assertions.assertNull(authTokenDAO.get("token1"));
        Assertions.assertNull(authTokenDAO.get("token2"));
    }
}
