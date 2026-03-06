package server.handlers;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.results.CreateGameResult;
import server.results.ErrorResult;
import server.results.ListGamesResult;
import service.GameService;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("Authorization");
        var games = gameService.listGames(authToken);
        String jsonResponse = gson.toJson(new ListGamesResult(games));
        ctx.status(200);
        ctx.result(jsonResponse);

    }

    public void createGame(Context ctx) throws UnauthorizedException, DataAccessException, BadRequestException {
        String authToken = ctx.header("Authorization");
        CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
        int gameID = gameService.createGame(authToken, req.gameName());
        ctx.status(200);
        ctx.result(gson.toJson(new CreateGameResult(gameID)));
    }

    public void joinGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class);
        try {
            gameService.joinGame(authToken, req.playerColor(), req.gameId());
            ctx.status(200);
            ctx.json(java.util.Map.of());
        } catch (Exception e) {
            ctx.status(403);
            ctx.json(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
