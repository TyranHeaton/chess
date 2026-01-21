package chess;
import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        int[] rowOffsets = {2,2,-2,-2,1,1,-1,-1};
        int[] colOffsets = {1,-1,1,-1,2,-2,2,-2};


        return validMoves;
    }
}
