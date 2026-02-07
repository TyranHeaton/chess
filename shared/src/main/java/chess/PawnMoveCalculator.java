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
                if (targetRow == 1 || targetRow == 8) {
                    validMoves.add(new ChessMove(position, targetPosition, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, targetPosition, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, targetPosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, targetPosition, ChessPiece.PieceType.KNIGHT));
                }
                else {
                    validMoves.add(new ChessMove(position, targetPosition, null));
                }
                if (currentRow == 2 && myColor == ChessGame.TeamColor.WHITE) {
                    int twoStepCheck = currentRow + (2 * direction);
                    ChessPosition twoStepPosition = new ChessPosition(twoStepCheck, currentCol);
                    if (board.getPiece(twoStepPosition) == null) {
                        validMoves.add(new ChessMove(position, twoStepPosition, null));
                    }
                }
                else if (currentRow == 7 && myColor == ChessGame.TeamColor.BLACK) {
                    int twoStepCheck = currentRow + (2 * direction);
                    ChessPosition twoStepPosition = new ChessPosition(twoStepCheck, currentCol);
                    if (board.getPiece(twoStepPosition) == null) {
                        validMoves.add(new ChessMove(position, twoStepPosition, null));
                    }
                }
            }
        }
        int targetDiagonalRow = currentRow + direction;
        int targetDiagonalCol = currentCol + 1;
        if (targetDiagonalRow < 9 && targetDiagonalRow > 0 && targetDiagonalCol < 9 && targetDiagonalCol > 0) {
            ChessPosition diagonalTarget1 = new ChessPosition(targetDiagonalRow, targetDiagonalCol);
            ChessPiece pieceAtDiagonalTarget1 = board.getPiece(diagonalTarget1);
            if (pieceAtDiagonalTarget1 != null && pieceAtDiagonalTarget1.getTeamColor() != myColor ) {
                if (targetDiagonalRow == 8 || targetDiagonalRow == 1) {
                    validMoves.add(new ChessMove(position, diagonalTarget1, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, diagonalTarget1, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, diagonalTarget1, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, diagonalTarget1, ChessPiece.PieceType.KNIGHT));
                }
                else {
                    validMoves.add(new ChessMove(position, diagonalTarget1, null));
                }
            }
        }

        targetDiagonalCol = currentCol - 1;
        if (targetDiagonalRow < 9 && targetDiagonalRow > 0 && targetDiagonalCol < 9 && targetDiagonalCol > 0) {
            ChessPosition diagonalTarget2 = new ChessPosition(targetDiagonalRow, targetDiagonalCol);
            ChessPiece pieceAtDiagonalTarget2 = board.getPiece(diagonalTarget2);
            if (pieceAtDiagonalTarget2 != null && pieceAtDiagonalTarget2.getTeamColor() != myColor) {
                if (targetDiagonalRow == 8 || targetDiagonalRow == 1) {
                    validMoves.add(new ChessMove(position, diagonalTarget2, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, diagonalTarget2, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, diagonalTarget2, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, diagonalTarget2, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, diagonalTarget2, null));
                }
            }
        }
        return validMoves;
    }
}
