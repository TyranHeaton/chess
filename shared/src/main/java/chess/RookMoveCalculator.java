package chess;
import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {1,-1,0,0};
        int[] colOffsets = {0,0,1,-1};
        for (int i = 0; i < 4; i++){
            int currentRow = position.getRow();
            int currentCol = position.getColumn();
            while(true){
                currentRow = currentRow + rowOffsets[i];
                currentCol = currentCol + colOffsets[i];
                if (currentRow > 8 || currentRow < 1 || currentCol > 8 || currentCol < 1){
                    break;
                }
                ChessPosition targetPosition = new ChessPosition(currentRow, currentCol);
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
