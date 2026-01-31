package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {2,2,-2,-2,1,1,-1,-1};
        int[] colOffsets = {1,-1,1,-1,2,-2,2,-2};
        for (int i = 0; i < 8; i++){
            int targetRow = currentRow + rowOffsets[i];
            int targetCol = currentCol + colOffsets[i];
            if (targetRow > 0 && targetRow < 9 && targetCol > 0 && targetCol < 9){
                ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);
                ChessPiece pieceAtTarget = board.getPiece(targetPosition);
                if (pieceAtTarget == null || pieceAtTarget.getTeamColor() != myColor){
                    validMoves.add(new ChessMove(position, targetPosition, null));
                }
            }
        }
        return validMoves;
    }
}
