package server.handlers;

import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.results.ErrorResult;
import server.results.ListGamesResult;
import service.GameService;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx){
        String authToken = ctx.header("Authorization");
        try {
            var games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.json(new ListGamesResult(games));
        } catch (Exception e) {
            ctx.status(401);
            ctx.json(new ErrorResult("Error: unauthorized"));
        }
    }

    public void createGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        CreateGameRequest req = ctx.bodyAsClass(CreateGameRequest.class);
        try {
            int gameId = gameService.createGame(authToken, req.gameName());
            ctx.status(200);
            ctx.json(gameId);
        } catch (Exception e) {
            ctx.status(401);
            ctx.json(new ErrorResult("Error: unauthorized"));
        }
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
