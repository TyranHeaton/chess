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
            gatherMove(board, position, myColor, rowOffsets[i], colOffsets[i], validMoves );
        }
        return validMoves;
    }
}
