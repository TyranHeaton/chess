package server;
import com.google.gson.Gson;
import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import io.javalin.Javalin;
import server.handlers.ClearHandler;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MySQLUserDAO();
        GameDAO gameDAO = new MySQLGameDAO();
        AuthDAO authDAO = new MySQLAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");

            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setIdleTimeout(java.time.Duration.ofHours(1));
            });
        });

        javalin.ws("/ws", ws -> new WebSocketHandler(ws, authDAO, gameDAO));

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.delete("/db", clearHandler::clear);

        registerExceptions();
    }

    private void registerExceptions() {
        // 401: Unauthorized (Bad password or bad token)
        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result(new Gson().toJson(new ErrorResponse("Error: Unauthorized")));
        });

        // 400: Bad Request (Missing fields or invalid color)
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result(new Gson().toJson(new ErrorResponse("Error: bad request")));
        });

        // 403: Already Taken (Username exists or color is full)
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result(new Gson().toJson(new ErrorResponse("Error: already taken")));
        });

        // 500: Database Failure
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result(new Gson().toJson(new ErrorResponse("Error: " + e.getMessage())));
        });
    }

    public int run(int desiredPort) {
        System.out.println("SERVER DEBUG: run() method started.");
        try {
            // Force the creation of the database
            DatabaseManager.createDatabase();
            System.out.println("SERVER DEBUG: DatabaseManager.createDatabase() finished.");

            // Force the creation of the tables
            MySQLUserDAO setupDAO = new MySQLUserDAO();
            setupDAO.configureDatabase();
            System.out.println("SERVER DEBUG: configureDatabase() finished successfully!");

        } catch (Exception e) {
            System.out.println("SERVER DEBUG: Database setup FAILED!");
            e.printStackTrace();
        }
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
