package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import websocket.WebSocketCommunicator;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;
import java.util.stream.Collectors;

import static ui.EscapeSequences.*;


public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String authToken = null;
    private boolean isWhitePerspective = true;
    private WebSocketCommunicator ws;
    private NotificationHandler notificationHandler;
    private Integer gameID;
    private final Scanner scanner = new Scanner(System.in);
    private ChessGame currentGame;
    private ChessGame.TeamColor playerColor;
    private final BoardDrawer drawer = new BoardDrawer();

    private final Map<Integer, Integer> gameListItemMap = new HashMap<>();

    public ChessClient()  {
        int port = 8080;
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
        if (command == null || command.trim().isEmpty()) {
            return help();
        }

        try {
            var parts = command.trim().split("\\s+");
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
                case "move" -> move(params);
                case "leave" -> leave();
                case "resign" -> resign();
                case "redraw" -> redraw();
                case "highlight" -> highlight(params);
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

        try {
            int uiID = Integer.parseInt(params[0]);

            if (!gameListItemMap.containsKey(uiID)) {
                throw new Exception("Please use list to see valid game IDs before trying to join.");
            }

            if (this.ws != null) {
                return "You are already in a game. Please 'leave' before joining another.";
            }

            int actualGameID = gameListItemMap.get(uiID);
            this.gameID = actualGameID;

            String colorParam = params[1].toUpperCase();

            this.playerColor = colorParam.equals("BLACK") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            this.isWhitePerspective = (this.playerColor == ChessGame.TeamColor.WHITE);

            server.joinGame(this.authToken, colorParam, actualGameID);

            ChessBoard board = new ChessBoard();
            board.resetBoard();
            boolean isWhite = colorParam.equalsIgnoreCase("WHITE");
            drawer.drawBoard(board, isWhite);

            this.ws = new WebSocketCommunicator(this);
            this.ws.connect(authToken, actualGameID);

            this.state = State.INGAME;

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
            int actualGameID = gameListItemMap.get(uiID);
            this.gameID = actualGameID;

            this.playerColor = null;
            this.isWhitePerspective = true;
            this.ws = new WebSocketCommunicator(this);
            this.ws.connect(authToken, actualGameID);
            this.state = State.INGAME;

            if (!gameListItemMap.containsKey(uiID)) {
                return "Please use list to see valid game IDs before trying to observe.";
            }
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            drawer.drawBoard(board, true);

            this.state = State.INGAME;

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

    private String move(String[] params) throws Exception {
        assertInGame();
        if (params.length < 2) {
            throw new Exception("Expected: <FROM> <TO>");
        }
        ChessPosition start = parsePosition(params[0]);
        ChessPosition end = parsePosition(params[1]);

        if (start == null || end == null) {
            throw new Exception("Invalid position format. Use format like e2 or d7.");
        }

        ChessMove move = new ChessMove(start, end, null);

        ws.makeMove(authToken, gameID, move);
        return "Move sent.";
    }

    private String leave() throws Exception {
        assertInGame();

        if (ws != null) {
            ws.leaveGame(authToken, gameID);
            this.ws = null;
        }

        this.state = State.LOGGED_IN;
        this.currentGame = null;
        this.gameID = null;
        this.playerColor = null;

        return "Left the game. Returning to Post-Login menu...";
    }

    private String resign() throws Exception {
        assertInGame();
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirmation = scanner.next().toLowerCase();

        if (confirmation.equals("yes")) {
            ws.resign(authToken, gameID);
            return "Resigning from the game...";
        } else {
            return "Resignation cancelled.";
        }
    }

    private String redraw() throws Exception {
        assertInGame();
        if (currentGame == null) {
            throw new Exception("No game state to redraw.");
        }
        assertLoggedIn();
        boolean isWhitePerspective = (playerColor == ChessGame.TeamColor.WHITE || playerColor == null);
        drawer.drawBoard(currentGame.getBoard(), isWhitePerspective);
        return "Board redrawn.";
    }

    private String highlight(String[] params) throws Exception {
        assertInGame();
        if (params.length < 1) {
            throw new Exception("Expected: highlight <POSITION> (e.g. highlight e2)");
        }

        ChessPosition startPos = parsePosition(params[0]);

        Collection<ChessMove> validMoves = currentGame.validMoves(startPos);

        if (validMoves == null || validMoves.isEmpty()) {
            return "No legal moves for the piece at " + params[0];
        }

        Set<ChessPosition> highlightPositions = validMoves.stream().map(ChessMove::getEndPosition).collect(Collectors.toSet());

        highlightPositions.add(startPos);

        boolean isWhite = (playerColor == ChessGame.TeamColor.WHITE || playerColor == null);
        drawer.drawBoard(currentGame.getBoard(), isWhite, highlightPositions);

        return "Showing legal moves for " + params[0];
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
        if (state == State.LOGGED_IN) {
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
        else {
            return """
                    move <FROM> <TO> - to make a move (e.g. move e2 e4)
                    highlight <POSITION> - to show legal moves for a piece (e.g. highlight e2)
                    redraw - to redraw the board
                    leave - to leave the game and return to the post-login menu
                    resign - to resign from the game
                    quit - playing chess
                    help - with possible commands
                    """;
        }
    }

    private ChessPosition parsePosition(String pos) {
        try {
            if (pos == null || pos.length() != 2) return null;
            char file = Character.toLowerCase(pos.charAt(0));
            char rank = pos.charAt(1);
            if (file < 'a' || file > 'h' || rank < '1' || rank > '8') return null;
            int col = file - 'a' + 1;
            int row = rank - '0';
            return new ChessPosition(row, col);
        }
        catch (Exception e) {
            return null;
        }
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
        LOGGED_IN,
        INGAME
    }

    private void handleLoadGame(ChessGame game) {
        this.currentGame = game;
        System.out.println("\n" + SET_TEXT_COLOR_GREEN + "Current Game State:" + RESET_TEXT_COLOR);
        drawer.drawBoard(game.getBoard(), this.isWhitePerspective);
        printPrompt();
    }

    private void printPrompt() {
        String colorPrefix = isWhitePerspective ? "[WHITE]" : "[BLACK]";
        System.out.print("\n" + colorPrefix + " >>> ");
    }

    private void assertInGame() throws Exception {
        if (state != State.INGAME) {
            throw new Exception("You must be in a game to perform this action.");
        }
        if (ws == null || gameID == null) {
            throw new Exception("No active game connection.");
        }
    }

}
