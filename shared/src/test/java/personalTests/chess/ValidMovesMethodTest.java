package personalTests.chess;

import chess.*;
import org.junit.jupiter.api.*;
import java.util.Collection;

public class ValidMovesMethodTest {
    private ChessGame game = new ChessGame();
    private ChessBoard board = new ChessBoard();


    @Test
    public void testPinnedPiece() {
        board = new ChessBoard();
        game.setBoard(board);
        // Setup: White King (1,1), White Rook (2,1), Black Rook (8,1)
        // The White Rook is pinned vertically. It cannot move horizontally.
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(2, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        quickAdd(8, 1, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        Collection<ChessMove> moves = game.validMoves(new ChessPosition(2, 1));

        // On an empty board, a Rook at (2,1) has 14 moves.
        // In a pin, it should only have 7 (the vertical ones towards/at the attacker).
        Assertions.assertEquals(6, moves.size(), "Pinned Rook should only have vertical moves");
    }
    @Test
    public void mustEscapeCheck() {
        board = new ChessBoard();
        game.setBoard(board);

        // White King is in check by Black Queen.
        // White Knight is nearby but can only make ONE move to block the check.
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(1, 8, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        quickAdd(3, 3, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        Collection<ChessMove> knightMoves = game.validMoves(new ChessPosition(3, 3));

        // The Knight has many theoretical moves, but only ONE blocks the Queen.
        Assertions.assertEquals(2, knightMoves.size(), "Knight must block the check");
    }

    @Test
    public void pawnPromotionTest() {
        board = new ChessBoard();
        game.setBoard(board);

        // White Pawn at row 7, ready to move to row 8
        ChessPosition start = new ChessPosition(7, 2);
        quickAdd(7, 2, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        // Add a King so the game is valid
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        Collection<ChessMove> moves = game.validMoves(start);

        // One move to (8,2), but 4 options (Queen, Rook, Bishop, Knight)
        Assertions.assertEquals(4, moves.size(), "Pawn at row 7 should have 4 promotion moves");

        for (ChessMove move : moves) {
            Assertions.assertNotNull(move.getPromotionPiece(), "Promotion piece should not be null");
        }
    }
    @Test
    public void captureToEscapeCheck() {
        board = new ChessBoard();
        game.setBoard(board);

        // King is in check by a Bishop
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        quickAdd(3, 3, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP); // The Attacker

        // Rook can capture the Bishop
        ChessPosition rookPos = new ChessPosition(1, 3);
        quickAdd(1, 3, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        Collection<ChessMove> moves = game.validMoves(rookPos);

        // Rook has many moves, but only ONE (capturing at 3,3) saves the King
        Assertions.assertEquals(1, moves.size(), "Rook's only valid move is to capture the attacker");
        ChessMove move = moves.iterator().next();
        Assertions.assertEquals(new ChessPosition(3, 3), move.getEndPosition());
    }
    @Test
    public void kingCantMoveIntoCheck() {
        board = new ChessBoard();
        game.setBoard(board);

        // King is safe at (1,1)
        ChessPosition kingPos = new ChessPosition(1, 1);
        quickAdd(1, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        // Black Rook covers the 2nd rank (row 2)
        quickAdd(2, 8, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        Collection<ChessMove> moves = game.validMoves(kingPos);

        // King normally has 3 moves: (1,2), (2,1), (2,2)
        // But (2,1) and (2,2) are under attack by the Rook!
        // Only (1,2) is safe.
        Assertions.assertEquals(1, moves.size(), "King should only have 1 safe move remaining");
    }
    private void quickAdd(int r, int c, ChessGame.TeamColor t, ChessPiece.PieceType p) {
        board.addPiece(new ChessPosition(r, c), new ChessPiece(t, p));
    }
}

