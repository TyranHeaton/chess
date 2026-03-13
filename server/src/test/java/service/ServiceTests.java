package service;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.UserData;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chess.ChessGame;
import server.requests.CreateGameRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @BeforeEach
    public void setup(){
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void clearPositiveTest() throws DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", new ChessGame());
        AuthData authData = new AuthData("testToken-123", "testUser");
        userDAO.insert(userData);
        gameDAO.insert(gameData);
        authDAO.insert(authData);
        clearService.clear();
        assertNull(userDAO.get(userData.username())); // Verify user is gone
        assertTrue(gameDAO.listGames().isEmpty(), "Game list should be empty after clear");
        assertNull(authDAO.get("token-123"), "Auth token should be null after clear");
    }

    @Test
    public void registerPositiveTest() throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        AuthData authData = userService.register(userData);
        assertEquals(authData.username(), userData.username(), "Username in response should match username in request");
        assertNotNull(authData.authToken(), "Auth token should not be null");
    }

    @Test
    public void registerNegativeTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        userService.register(userData);
        assertThrows(Exception.class, () -> {
            userService.register(new UserData("testUser", "testPassword2", "test@Email2"));
        });
    }

    @Test
    public void loginPositiveTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData ("testUser", "testPassword", "test@Email");
        userService.register(userData);
        AuthData authData = userService.login(userData.username(), userData.password());
        assertEquals(authData.username(), userData.username(), "Username in response should match username in request");
        assertNotNull(authData.authToken(), "Auth token should not be null");
    }

    @Test
    public void loginNegativeTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData ("testUser", "testPassword", "test@Email");
        userService.register(userData);
        assertThrows(Exception.class, () -> {
            userService.login(userData.username(), "wrongPassword");
        });
    }

    @Test
    public void logoutPositiveTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        userService.register(userData);
        AuthData authData = userService.login(userData.username(), userData.password());
        String token = authData.authToken();
        userService.logout(token);
        assertNull(authDAO.get(token), "Auth token should be null after logout");
    }

    @Test
    public void logoutNegativeTest() {
        String fakeToken = "fakeToken";
        assertThrows(Exception.class, () -> {
            userService.logout(fakeToken);
        });
    }

    @Test
    public void joinGamePositiveTest() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        userService.register(userData);
        AuthData authData = userService.login(userData.username(), userData.password());
        String authToken = authData.authToken();
        int gameID = gameService.createGame(authToken, "testGame");
        gameService.joinGame(authToken, "white", gameID);
        GameData gameData = gameDAO.get(String.valueOf(gameID));
        assertEquals("testUser", gameData.whiteUsername(), "White player should be testUser");
    }

    @Test
    public void joinGameNegativeTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        userService.register(userData);
        AuthData authData = userService.login(userData.username(), userData.password());
        int gameID = gameService.createGame(authData.authToken(), "testGame");
        gameService.joinGame(authData.authToken(), "white", gameID);
        UserData userData2 = new UserData("testUser2", "testPassword2", "test2@Email");
        userService.register(userData2);
        AuthData authData2 = userService.login(userData2.username(), userData2.password());
        assertThrows(Exception.class, () -> {
            gameService.joinGame(authData2.authToken(), "white", gameID);
        });
    }

    @Test
    public void createGamePositiveTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        	UserData userData = new UserData("testUser", "testPassword", "test@Email");
            userService.register(userData);
            AuthData authData = userService.login(userData.username(), userData.password());
            String authToken = authData.authToken();
            int gameID = gameService.createGame(authToken, "testGame");
            assertTrue(gameID >= 0, "Game ID should be non-negative");
    }

    @Test
    public void createGameNegativeTest() {
        String fakeToken = "fakeToken";
        CreateGameRequest request = new CreateGameRequest("testGame");
        assertThrows(Exception.class, () -> {
            gameService.createGame(fakeToken, request.gameName());
        });
    }

    @Test
    public void listGamesPositiveTest() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userData = new UserData("testUser", "testPassword", "test@Email");
        userService.register(userData);
        AuthData authData = userService.login(userData.username(), userData.password());
        String authToken = authData.authToken();
        gameService.createGame(authToken, "testGame1");
        gameService.createGame(authToken, "testGame2");
        gameService.createGame(authToken, "testGame3");
        assertEquals(3, gameService.listGames(authToken).size());
    }

    @Test
    public void listGamesNegativeTest() {
        String wrongToken = "wrongToken";
        assertThrows(Exception.class, () -> {
            gameService.listGames(wrongToken);
        });
    }
}
