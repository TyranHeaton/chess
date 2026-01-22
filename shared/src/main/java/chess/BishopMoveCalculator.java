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
            gatherMove(board, position, myColor, rowOffsets[i], colOffsets[i], validMoves);
        }
        return validMoves;
    }
}
