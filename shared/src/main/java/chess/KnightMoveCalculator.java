package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {2,2,-2,-2,1,1,-1,-1};
        int[] colOffsets = {1,-1,1,-1,2,-2,2,-2};

        for (int i = 0; i < 8; i++){
            addSingleStepMove(board, position, myColor, rowOffsets[i], colOffsets[i], validMoves);
        }
        return validMoves;
    }
}
