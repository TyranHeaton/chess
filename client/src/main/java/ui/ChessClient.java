package ui;

import model.AuthData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ChessClient {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String authToken = null;

    private final Map<Integer, Integer> gameListItemMap = new HashMap<>();

    public ChessClient(int port) {
        server = new ServerFacade(port);
    }

    public String evaluateCommand(String command) {
        try {
            var parts = command.split(" ");
            var cmd = parts[0].toLowerCase();
            var params = Arrays.copyOfRange(parts, 1, parts.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> joinGame(params, false);
                case "observe" -> joinGame(params, true);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }

    }

    public State getState() {
        return state;
    }

    private String register(String[] params) throws Exception {
        if (params.length == 3) {
            AuthData authData = server.register(params[0], params[1], params[2]);
            this.authToken = authData.authToken();
            state = State.LOGGED_IN;
            return "Registered successfully! You are logged in as " + params[0] + ".\n";
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String[] params) throws Exception {
        if (params.length == 2) {
            AuthData authData = server.login(params[0], params[1]);
            this.authToken = authData.authToken();
            state = State.LOGGED_IN;
            return "Logged in successfully as " + params[0];
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    private String createGame(String[] params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            server.createGame(this.authToken, params[0]);
            return "Game created! ";
        }
        throw new Exception("Expected: <NAME>");
    }

    private String listGames() throws Exception {
        assertLoggedIn();
        var games = server.listGames(this.authToken);
        gameListItemMap.clear();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < games.length; i++) {
            int uiID = i + 1;
            gameListItemMap.put(uiID, games[i].gameID());
            sb.append(String.format("%d: %s (White: %s, Black: %s)\n",
                    uiID, games[i].gameName(), games[i].whiteUsername(), games[i].blackUsername()));
        }
        return sb.toString();
    }

    private String joinGame(String[] params, boolean isObserver) throws Exception {
        assertLoggedIn();
        if (params.length >= 1) {
            int uiID = Integer.parseInt(params[0]);
            Integer gameID = gameListItemMap.get(uiID);
            if (gameID == null) {
                throw new Exception("Invalid game ID.");
            }

            String playerColor = null;
            if (!isObserver) {
                if (params.length < 2) {
                    throw new Exception("Expected: <ID> [WHITE|BLACK]");
                }
                playerColor = params[1].toUpperCase();
            }

            server.joinGame(this.authToken, playerColor, gameID);

            chess.ChessBoard board = new chess.ChessBoard();
            board.resetBoard();

            boolean isWhite = isObserver || params[1].equalsIgnoreCase("WHITE");
            BoardDrawer.drawBoard(board, isWhite);

            return "Joined game " + gameID + " as " + (isObserver ? "OBSERVER" : playerColor);
        }
        throw new Exception("Expected: <ID> [WHITE|BLACK]");
    }

    private String logout() throws Exception {
        assertLoggedIn();
        server.logout(this.authToken);
        state = State.LOGGED_OUT;
        return "Logged out successfully.";
    }


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

    public enum State {
        LOGGED_OUT,
        LOGGED_IN
    }
}
