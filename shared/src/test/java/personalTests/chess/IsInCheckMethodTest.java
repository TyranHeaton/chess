package personalTests.chess;

import chess.*;
import org.junit.jupiter.api.*;

public class IsInCheckMethodTest {
    private ChessGame game = new ChessGame();
    private ChessBoard board = new ChessBoard();

    @Test
    public void testEssentialCases() {
        // Case 1: Rook Direct (Vertical)
        board = new ChessBoard();
        game.setBoard(board);
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(8, 1, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), "Rook should check King");

        // Case 2: Knight Jump (L-shape)
        board = new ChessBoard();
        game.setBoard(board);
        quickAdd(4, 4, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(6, 5, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        Assertions.assertTrue(game.isInCheck(ChessGame.TeamColor.WHITE), "Knight should check King");

        // Case 3: Blocked (Safe)
        board = new ChessBoard();
        game.setBoard(board);
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(1, 8, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK); // Attacker
        quickAdd(1, 4, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN); // The Shield
        Assertions.assertFalse(game.isInCheck(ChessGame.TeamColor.WHITE), "Pawn should block Rook attack");
    }

    private void quickAdd(int r, int c, ChessGame.TeamColor t, ChessPiece.PieceType p) {
        board.addPiece(new ChessPosition(r, c), new ChessPiece(t, p));
    }
}