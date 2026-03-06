package server.handlers;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.AuthResult;
import server.results.ErrorResult;
import service.UserService;
import io.javalin.http.Context;
import exceptions.*;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        UserData userData = new UserData(req.username(), req.password(), req.email());
        AuthData authData = userService.register(userData);
        ctx.status(200);
        ctx.result(gson.toJson(new AuthResult(authData.username(), authData.authToken())));
    }

    public void logout(Context ctx) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        String authToken = ctx.header("Authorization");
        userService.logout(authToken);
        ctx.status(200);
        ctx.result("{}");
    }

    public void login(Context ctx) throws BadRequestException, DataAccessException, UnauthorizedException {
        LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
        AuthData auth = userService.login(req.username(), req.password());
        ctx.status(200);
        ctx.result(gson.toJson(new AuthResult(auth.username(), auth.authToken())));
    }
}
