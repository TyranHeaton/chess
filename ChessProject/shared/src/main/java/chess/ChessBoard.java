package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * the signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        row = row - 1;
        int col = position.getColumn();
        col = col - 1;
        this.board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        row = row - 1;
        int col = position.getColumn();
        col = col - 1;
        return this.board[row][col];

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[8][8];
        //Set up white and black pawns
        for (int column_index = 1; column_index <= 8; column_index++) {
            addPiece(new ChessPosition(2, column_index), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, column_index), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //Set up white rooks
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        //Set up white knights
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        //Set up white bishops
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        //Set up white queen
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        //Set up white king
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        //Set up black rooks
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        //Set up white knights
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        //Set up white bishops
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        //Set up white queen
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        //Set up white king
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));



    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder board_visualization = new StringBuilder();
        for (int row_index = 8; row_index >= 1; row_index--){
            for (int col_index = 1; col_index <= 8; col_index++){
                ChessPiece piece = getPiece(new ChessPosition(row_index, col_index));
                if (piece == null){
                    board_visualization.append("| |");
                }
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){

                        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                            board_visualization.append("|P|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
                            board_visualization.append("|R|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                            board_visualization.append("|B|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                            board_visualization.append("|N|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.KING){
                            board_visualization.append("|K|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                            board_visualization.append("|Q|");
                        }
                    }
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                            board_visualization.append("|p|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
                            board_visualization.append("|r|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                            board_visualization.append("|b|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                            board_visualization.append("|n|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.KING){
                            board_visualization.append("|k|");
                        }
                        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
                            board_visualization.append("|q|");
                        }
                    }
                }
            }
            board_visualization.append("\n");
        }
        return board_visualization.toString();
    }
}
