package chess;
import java.util.Collection;
import java.util.ArrayList;

public class PawnMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            throw new RuntimeException("Not implemented");
        }
        else {
            throw new RuntimeException("Not Implemented");
        }


    }
}
