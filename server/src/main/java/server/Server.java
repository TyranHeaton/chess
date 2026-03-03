package server;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.handlers.ClearHandler;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.ClearService;
import server.ErrorResponse;
import service.GameService;
import service.UserService;

public class Server {
    private final Javalin javalin;
    private ClearService clearService;


    public Server() {
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Create javalin instance
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::handleClearDatabase);

        // TODO: Finish registering endpoints


    }
    public void handleClearDatabase(Context ctx){
        try {
            clearService.clear();
            ctx.status(200).json(new Object());
        } catch (DataAccessException e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
