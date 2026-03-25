package ui;

import chess.ChessBoard;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import static java.lang.System.out;

public class BoardDrawer {
    private static final PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public static void drawBoard(ChessBoard board, boolean isWhitePerspective) {
        drawHeaders(isWhitePerspective);

        if (isWhitePerspective) {
            for (int row = 8; row >= 1; row--) {
                drawRow(board, row, true);
            }
        }
        //TODO: Complete method
    }

    public static void drawHeaders(boolean isWhitePerspective) {
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

    public static void drawRow(ChessBoard board, int row, boolean isWhitePerspective) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " ");

        //Columns
        if (isWhitePerspective) {
            for (int c = 1; c <= 8; c++) {
                drawSquare(board, row, c);
            }
        }
        //TODO: Complete method
    }

    public static void drawSquare(ChessBoard board, int row, int col) {
        //TODO: Implement method
    }
}
