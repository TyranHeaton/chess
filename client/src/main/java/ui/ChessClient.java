package ui;



import model.AuthData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ChessClient {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    private Map<Integer, Integer> gameListItemMap = new HashMap<>();

    public ChessClient(int port){
        server = new ServerFacade(port);
    }

    public String evaluateCommand(String command) {
        try {
            var parts = command.toLowerCase().split(" ");
            var cmd = parts[0];
            var params = Arrays.copyOfRange(parts, 1, parts.length);

            return switch (cmd) {
                case "register" -> register(params);
                //TODO: Complete other cases
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }

    }

    private String register(String[] params) throws Exception {
        if (params.length == 3) {
            AuthData authData = server.register(params[0], params[1], params[2]);
            var authToken = authData.authToken();
            state = State.LOGGED_IN;
            return "Registered successfully! You are logged in as " + params[0];
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String[] params) throws Exception {
        if (params.length == 2) {
            AuthData authData = server.login(params[0], params[1]);
            var authToken = authData.authToken();
            state = State.LOGGED_IN;
            return "Logged in successfully as " + params[0];
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    private String createGame(String[] params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            server.createGame(null, params[0]);
            return "Game created! ";
        }
        throw new Exception("Expected: <NAME>");
    }

    private String listGames() throws Exception {
        assertLoggedIn();
        var games = server.listGames(null);
        gameListItemMap.clear();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < games.length; i++) {
            int uiID = i + 1;
            gameListItemMap.put(uiID, games[i].gameID());
            sb.append(String.format("%d: %s (White: %s, Black: %s)\n", uiID, games[i].gameName(), games[i].whiteUsername(), games[i].blackUsername()));
        }
        return sb.toString();
    }

    private String joinGame(String[] params) throws Exception {
        assertLoggedIn();
        if (params.length >= 1) {
            int uiID = Integer.parseInt(params[0]);
            //TODO: Complete this method
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    //TODO: Complete other methods

    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
            create <NAME> - a game
            list - games
            play <ID> [WHITE|BLACK] - a game
            observe <ID> - a game
            logout - when you are done
            quit - playing chess
            help - with possible commands
            """;
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You must be logged in to perform this action.");
        }
    }

    enum State {
        LOGGED_OUT,
        LOGGED_IN
    }
}
