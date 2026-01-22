package chess;
import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {0,0,1,1,1,-1,-1,-1};
        int[] colOffsets = {-1,1,-1,0,1,-1,0,1};
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        for (int i = 0; i < 8; i++){
            int targetRow = currentRow + rowOffsets[i];
            int targetCol = currentCol + colOffsets[i];
            if (targetRow > 0 && targetRow < 9 && targetCol < 9 && targetCol > 0){
                ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);
                ChessPiece pieceAtTarget = board.getPiece(targetPosition);
                if (pieceAtTarget == null || pieceAtTarget.getTeamColor() != myColor) {
                    validMoves.add(new ChessMove(position, targetPosition, null));
                }
            }
        }
        return validMoves;
    }
}
