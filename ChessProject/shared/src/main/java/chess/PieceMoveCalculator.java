package chess;
import java.util.Collection;


public interface PieceMoveCalculator {
    Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position);
    default void gatherMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor myColor, int rowOffset, int colOffset, Collection<ChessMove> validMoves){
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        while(true) {
            currentCol = currentCol + rowOffset;
            currentRow = currentRow + colOffset;
            if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
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
}
