package server.handlers;

import model.AuthData;
import model.UserData;
import server.requests.RegisterRequest;
import server.results.AuthResult;
import server.results.ErrorResult;
import service.UserService;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx){
        RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
        UserData userData = new UserData(req.username(), req.password(), req.email());
        try {
            AuthData authData = userService.register(userData);
            ctx.json(new AuthResult(authData.username(), authData.authToken()));
        } catch (Exception e) {
            ctx.status(403);
            ctx.json(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        String authToken = ctx.header("Authorization");
        try {
            userService.logout(authToken);
            ctx.status(200);
            ctx.json(java.util.Map.of());
        } catch (Exception e) {
            ctx.status(401);
            ctx.json(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
