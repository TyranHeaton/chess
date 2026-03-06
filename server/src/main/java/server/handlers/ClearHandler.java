package server.handlers;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import service.ClearService;
import io.javalin.http.Context;


public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;

    }

    public void clear(Context ctx) throws DataAccessException {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}"); // Success requires an empty JSON object
        } catch (DataAccessException e) {
            ctx.status(500);
        }

    }
}
