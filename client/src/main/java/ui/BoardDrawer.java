package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static java.lang.System.out;

public class BoardDrawer {
    private static final PrintStream PRINT_STREAM = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public void drawBoard(ChessBoard board, boolean isWhitePerspective) {
        this.drawBoard(board, isWhitePerspective, null);
    }

    public void drawBoard(ChessBoard board, boolean isWhitePerspective, Collection<ChessPosition> highlights) {
        drawHeaders(isWhitePerspective);

        if (isWhitePerspective) {
            for (int row = 8; row >= 1; row--) {
                drawRow(board, row, true, highlights);
            }
        }
        else {
            for (int row = 1; row <= 8; row++) {
                drawRow(board, row, false, highlights);
            }
        }
        drawHeaders(isWhitePerspective);
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public void drawHeaders(boolean isWhitePerspective) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        out.print("   ");

        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        if (!isWhitePerspective) {
            headers = new String[]{" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};
        }

        for (String header : headers) {
            out.print(header);
        }
        out.println("   " + RESET_BG_COLOR);
    }

    public void drawRow(ChessBoard board, int row, boolean isWhitePerspective, Collection<ChessPosition> highlights) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " ");

        //Columns
        if (isWhitePerspective) {
            for (int c = 1; c <= 8; c++) {
                drawSquare(board, row, c, highlights);
            }
        }
        else {
            for (int c = 8; c >= 1; c--) {
                drawSquare(board, row, c, highlights);
            }
        }

        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " ");
        out.println(RESET_BG_COLOR);
    }

    public void drawSquare(ChessBoard board, int row, int col, Collection<ChessPosition> highlights) {
        ChessPosition currentPos = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(currentPos);

        if ((row + col) % 2 == 0) {
            out.print(SET_BG_COLOR_BLACK);
        }
        else {
            out.print(SET_BG_COLOR_WHITE);
        }

        if (highlights != null && highlights.contains(currentPos)) {
            if ((row + col) % 2 == 0) {
                out.print(SET_BG_COLOR_GREEN);
            }
            else {
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
        }

        if (piece == null) {
            out.print("   ");
        }
        else {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_RED + getPieceChar(piece));
            }
            else {
                out.print(SET_TEXT_COLOR_BLUE + getPieceChar(piece));
            }
        }

    }

    public String getPieceChar(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case ROOK -> " R ";
            case PAWN -> " P ";
        };
    }
}
