package chess;
import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {1,-1,1,-1};
        int[] colOffsets = {1,-1,-1,1};
        for (int i = 0; i < 4; i++){
            int currentRow = position.getRow();
            int currentCol = position.getColumn();
            while(true){
                currentCol = currentCol + rowOffsets[i];
                currentRow = currentRow + colOffsets[i];
                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8){
                    break;
                }
                ChessPosition targetPosition = new ChessPosition(currentRow,currentCol);
                ChessPiece pieceAtTarget = board.getPiece(targetPosition);
                if (pieceAtTarget == null){
                    validMoves.add(new ChessMove(position, targetPosition, null));
                }
                else if (pieceAtTarget.getTeamColor() != myColor){
                    validMoves.add(new ChessMove(position, targetPosition, null));
                    break;
                }
                else {
                    break;
                }
            }
        }
        return validMoves;
    }
}
