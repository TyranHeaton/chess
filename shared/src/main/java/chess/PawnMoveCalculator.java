package chess;
import java.util.Collection;
import java.util.ArrayList;

public class PawnMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        int direction = 1;
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        if (myColor == ChessGame.TeamColor.BLACK){
            direction = -1;
        }
        int targetRow = currentRow + direction;
        if (targetRow > 0 && targetRow < 9 && currentCol > 0 && currentCol < 9) {
            ChessPosition targetPosition = new ChessPosition(targetRow, currentCol);
            ChessPiece pieceAtTarget = board.getPiece(targetPosition);
            if (pieceAtTarget == null) {
                addMoveOrPromotions(position, targetPosition, targetRow, validMoves);
                boolean atStartRow = (currentRow == 2 && myColor == ChessGame.TeamColor.WHITE)
                        || (currentRow == 7 && myColor == ChessGame.TeamColor.BLACK);
                addTwoStepMoveIfValid(board, position, currentRow, currentCol, direction, atStartRow, validMoves);
            }
        }

        addDiagonalCaptureIfValid(board, position, currentRow, currentCol, direction, myColor, 1, validMoves);
        addDiagonalCaptureIfValid(board, position, currentRow, currentCol, direction, myColor, -1, validMoves);
        return validMoves;
    }

    private void addTwoStepMoveIfValid(ChessBoard board, ChessPosition position, int currentRow, int currentCol,
                                       int direction, boolean atStartRow, Collection<ChessMove> validMoves) {
        if (!atStartRow) {
            return;
        }

        int twoStepCheck = currentRow + (2 * direction);
        ChessPosition twoStepPosition = new ChessPosition(twoStepCheck, currentCol);
        if (board.getPiece(twoStepPosition) == null) {
            validMoves.add(new ChessMove(position, twoStepPosition, null));
        }
    }

    private void addDiagonalCaptureIfValid(ChessBoard board, ChessPosition position, int currentRow, int currentCol,
                                           int direction, ChessGame.TeamColor myColor, int diagonalColOffset,
                                           Collection<ChessMove> validMoves) {
        int targetDiagonalRow = currentRow + direction;
        int targetDiagonalCol = currentCol + diagonalColOffset;
        if (targetDiagonalRow < 9 && targetDiagonalRow > 0 && targetDiagonalCol < 9 && targetDiagonalCol > 0) {
            ChessPosition diagonalTarget = new ChessPosition(targetDiagonalRow, targetDiagonalCol);
            ChessPiece pieceAtDiagonalTarget = board.getPiece(diagonalTarget);
            if (pieceAtDiagonalTarget != null && pieceAtDiagonalTarget.getTeamColor() != myColor) {
                addMoveOrPromotions(position, diagonalTarget, targetDiagonalRow, validMoves);
            }
        }
    }

    private void addMoveOrPromotions(ChessPosition start, ChessPosition end, int endRow,
                                     Collection<ChessMove> validMoves) {
        if (endRow == 1 || endRow == 8) {
            validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        } else {
            validMoves.add(new ChessMove(start, end, null));
        }
    }
}
