package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {
    private static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() throws Exception {
        // This runs before EVERY @Test method
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTestPositive() throws Exception {
        AuthData authData = serverFacade.register("tester", "password1", "test@email1");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("tester", authData.username());
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    public void registerTestNegative() {
        assertThrows(Exception.class, () -> {
            serverFacade.register("tester", "password1", "test@email1");
            serverFacade.register("tester", "password2", "test@email2");
        });
    }

    @Test
    public void loginTestPositive() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("tester", authData.username());
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    public void loginTestNegative() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        assertThrows(Exception.class, () -> {
            serverFacade.login("tester", "wrongpassword");
        });
    }

    @Test
    public void logoutTestPositive() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        serverFacade.logout(authData.authToken());
        assertThrows(Exception.class, () -> {
            serverFacade.logout(authData.authToken());
        });
    }

    @Test
    public void logoutTestNegative() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        assertThrows(Exception.class, () -> {
            serverFacade.logout("invalidtoken");
        });
    }

    @Test
    public void createGameTestPositive() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        int gameID = serverFacade.createGame(authData.authToken(), "Test Game");
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameTestNegative() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        assertThrows(Exception.class, () -> {
            serverFacade.createGame("invalidtoken", "Test Game");
        });
    }

    @Test
    public void listGamesTestPositive() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        int gameID = serverFacade.createGame(authData.authToken(), "Test Game");
        Assertions.assertTrue(gameID > 0);
        var games = serverFacade.listGames(authData.authToken());
        Assertions.assertEquals(1, games.length);
        Assertions.assertEquals("Test Game", games[0].gameName());
    }

    @Test
    public void listGamesTestNegative() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        assertThrows(Exception.class, () -> {
            serverFacade.listGames("invalidtoken");
        });
    }

    @Test
    public void joinGameTestPositive() throws Exception {
        serverFacade.register("tester1", "password1", "test1@email");
        serverFacade.register("tester2", "password2", "test2@email");
        AuthData authData1 = serverFacade.login("tester1", "password1");
        AuthData authData2 = serverFacade.login("tester2", "password2");
        int gameID = serverFacade.createGame(authData1.authToken(), "Test Game");
        assertDoesNotThrow(() -> serverFacade.joinGame(authData2.authToken(), "black", gameID));
    }

    @Test
    public void joinGameTestNegative() throws Exception {
        serverFacade.register("tester1", "password1", "test1@email");
        serverFacade.register("tester2", "password2", "test2@email");
        AuthData auth1 = serverFacade.login("tester1", "password1");
        AuthData auth2 = serverFacade.login("tester2", "password2");
        int gameID = serverFacade.createGame(auth1.authToken(), "Test Game");
        serverFacade.joinGame(auth1.authToken(), "WHITE", gameID);
        assertThrows(Exception.class, () -> {
            serverFacade.joinGame(auth2.authToken(), "WHITE", gameID);
        });
    }
}
