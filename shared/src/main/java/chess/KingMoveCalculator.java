package chess;
import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {0,0,1,1,1,-1,-1,-1};
        int[] colOffsets = {-1,1,-1,0,1,-1,0,1};

        for (int i = 0; i < 8; i++){
            addSingleStepMove(board, position, myColor, rowOffsets[i], colOffsets[i], validMoves);
        }
        return validMoves;
    }
}
