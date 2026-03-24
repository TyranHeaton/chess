package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;


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
        Assertions.assertThrows(Exception.class, () -> {
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
        Assertions.assertThrows(Exception.class, () -> {
            serverFacade.login("tester", "wrongpassword");
        });
    }

    @Test
    public void logoutTestPositive() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        serverFacade.logout(authData.authToken());
        Assertions.assertThrows(Exception.class, () -> {
            serverFacade.logout(authData.authToken());
        });
    }

    @Test
    public void logoutTestNegative() throws Exception {
        serverFacade.register("tester", "password1", "test@email1");
        AuthData authData = serverFacade.login("tester", "password1");
        Assertions.assertNotNull(authData);
        Assertions.assertThrows(Exception.class, () -> {
            serverFacade.logout("invalidtoken");
        });
    }
}
