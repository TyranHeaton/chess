package server;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import server.ErrorResponse;

public class Server {
    private final Javalin javalin;
    private ClearService clearService;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        javalin.delete("/db", this::handleClearDatabase);
        // Register your endpoints and exception handlers here.

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
