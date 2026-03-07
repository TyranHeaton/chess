package chess;
import java.util.Collection;


public interface PieceMoveCalculator {
    Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position);

    default void gatherMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor myColor,
                            int rowOffset, int colOffset, Collection<ChessMove> validMoves){
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        while(true) {
            currentCol = currentCol + rowOffset;
            currentRow = currentRow + colOffset;
            if (!isInBounds(currentRow, currentCol)) {
                break;
            }
            ChessPosition targetPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtTarget = board.getPiece(targetPosition);
            if (pieceAtTarget == null) {
                validMoves.add(new ChessMove(position, targetPosition, null));
            } else if (pieceAtTarget.getTeamColor() != myColor) {
                validMoves.add(new ChessMove(position, targetPosition, null));
                break;
            } else {
                break;
            }
        }
    }

    default void addSingleStepMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor myColor,
                                   int rowOffset, int colOffset, Collection<ChessMove> validMoves) {
        int targetRow = position.getRow() + rowOffset;
        int targetCol = position.getColumn() + colOffset;
        if (!isInBounds(targetRow, targetCol)) {
            return;
        }

        ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);
        ChessPiece pieceAtTarget = board.getPiece(targetPosition);
        if (pieceAtTarget == null || pieceAtTarget.getTeamColor() != myColor) {
            validMoves.add(new ChessMove(position, targetPosition, null));
        }
    }

    default boolean isInBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }
}
