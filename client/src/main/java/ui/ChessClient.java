package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import websocket.WebSocketCommunicator;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.BoardDrawer.drawBoard;
import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String authToken = null;
    private WebSocketCommunicator ws;
    private final int port;
    private final String serverUrl;
    private ChessGame currentGame;
    private boolean isWhitePerspective = true;


    private final Map<Integer, Integer> gameListItemMap = new HashMap<>();

    public ChessClient(int port) {
        this.port = port;
        this.serverUrl = "http://localhost:" + port;
        server = new ServerFacade(port);

    }

    public void notify(ServerMessage message){
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(((LoadGameMessage) message).getGame());
            case NOTIFICATION -> handleNotification(((NotificationMessage) message).getMessage());
            case ERROR -> handleError(((ErrorMessage) message).getErrorMessage());
        }
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
                case "play" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception e) {
            return "Error: " + e.getMessage();
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

    private String joinGame(String[] params) throws Exception {
        assertLoggedIn();
        if (params.length < 2) {
            throw new Exception("Expected: <ID> [WHITE|BLACK]");
        }

        int uiID = Integer.parseInt(params[0]);

        if (!gameListItemMap.containsKey(uiID)) {
            throw new Exception("Please use list to see valid game IDs before trying to join.");
        }

        try {
            int actualGameID = gameListItemMap.get(uiID);
            String playerColor = params[1].toUpperCase();

            this.isWhitePerspective = !playerColor.equalsIgnoreCase("BLACK");

            server.joinGame(this.authToken, playerColor, actualGameID);

            ChessBoard board = new ChessBoard();
            board.resetBoard();
            boolean isWhite = playerColor.equalsIgnoreCase("WHITE");
            drawBoard(board, isWhite);

            ws = new WebSocketCommunicator(serverUrl, this);
            ws.connect(authToken, actualGameID);

            return "Joined game " + uiID + " as " + playerColor;
        }
        catch (NumberFormatException e) {
            throw new Exception("ID must be a number.");
        }
    }

    private String observeGame(String[] params) throws Exception {
        if (params.length < 1) {
            return "Expected: <ID>";
        }
        try {
            int uiID = Integer.parseInt(params[0]);

            if (!gameListItemMap.containsKey(uiID)) {
                return "Please use list to see valid game IDs before trying to observe.";
            }
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            drawBoard(board, true);

            return "Observing game " + uiID;
        }
        catch (NumberFormatException e) {
            return "ID must be a number.";
        }
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

    private void handleNotification(String notification) {
        System.out.println(SET_TEXT_COLOR_MAGENTA + notification + RESET_TEXT_COLOR);
        printPrompt();
    }

    private void handleError(String errorMessage) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + "!! " + errorMessage + " !!" + RESET_TEXT_COLOR);
        printPrompt();
    }

    public enum State {
        LOGGED_OUT,
        LOGGED_IN
    }

    private void handleLoadGame(ChessGame game) {
        // 1. Update your local game state
        this.currentGame = game;

        // 2. Clear the screen and draw the board (Phase 6 requirement)
        System.out.println("\n" + SET_TEXT_COLOR_GREEN + "Current Game State:" + RESET_TEXT_COLOR);
        drawBoard(game.getBoard(), this.isWhitePerspective);

        // 3. Reprint the prompt so the user knows they can type
        printPrompt();
    }

    private void printPrompt() {
        String colorPrefix = isWhitePerspective ? "[WHITE]" : "[BLACK]";
        System.out.print("\n" + colorPrefix + " >>> ");
    }
}
