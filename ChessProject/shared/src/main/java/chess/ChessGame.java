package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * the signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private ChessGame.TeamColor teamTurn;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();

    }
    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }
    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.WHITE){
            teamTurn = TeamColor.WHITE;
        }
        else {
            teamTurn = TeamColor.BLACK;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    /**
     * Gets a valid move for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for the requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessBoard currentBoard = getBoard();
        ChessPiece piece = currentBoard.getPiece(startPosition);

        Collection<ChessMove> possibleMoves = piece.pieceMoves(currentBoard, startPosition);
        for (ChessMove move : possibleMoves){
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece capturedPiece = currentBoard.getPiece(end);
            ChessPiece movingPiece = currentBoard.getPiece(start);
            currentBoard.addPiece(end, movingPiece);
            currentBoard.addPiece(start, null);
            if (!isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            currentBoard.addPiece(end, capturedPiece);
            currentBoard.addPiece(start, movingPiece);
        }
        return validMoves;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if the move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        ChessGame.TeamColor currentTurn = getTeamTurn();
        if (piece == null){
            throw new InvalidMoveException("No piece!");
        } else if (piece.getTeamColor() != currentTurn){
            throw new InvalidMoveException("Not your turn.");
        }
        Collection<ChessMove> validMoves = validMoves(start);
        if (validMoves == null || !validMoves.contains(move)){
            throw new InvalidMoveException("Not a valid move.");
        }
        if (move.getPromotionPiece() != null){
            ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
            ChessPiece promotionPiece = new ChessPiece(teamTurn, promotionPieceType);
            board.addPiece(end, promotionPiece);
            board.addPiece(start, null);
        }
        else {
            board.addPiece(end, piece);
            board.addPiece(start, null);
        }
        TeamColor nextTurn;
        if (currentTurn == TeamColor.WHITE){
            nextTurn = TeamColor.BLACK;
        }
        else {
            nextTurn = TeamColor.WHITE;
        }
        setTeamTurn(nextTurn);
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        kingSearch:
        for (int r = 1; r <= 8; r++){
            for (int c = 1; c < 9; c++) {
                ChessPosition temp_pos = new ChessPosition(r, c);
                ChessPiece pieceAtPos = board.getPiece(temp_pos);
                if (pieceAtPos != null) {
                    if (pieceAtPos.getPieceType() == ChessPiece.PieceType.KING && pieceAtPos.getTeamColor() == teamColor) {
                        kingPos = new ChessPosition(r, c);
                        break kingSearch;
                    }
                }
            }
        }
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition temp_pos = new ChessPosition(r, c);
                ChessPiece pieceAtPos = board.getPiece(temp_pos);
                if (pieceAtPos != null) {
                    if (pieceAtPos.getTeamColor() != teamColor) {
                        Collection<ChessMove> possibleEnemyMoves = pieceAtPos.pieceMoves(board, temp_pos);
                        for (ChessMove move : possibleEnemyMoves) {
                            ChessPosition endPosition = move.getEndPosition();
                            if (endPosition.equals(kingPos)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        return logicStalemateCheckmate(teamColor);
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        return logicStalemateCheckmate(teamColor);
    }
    /**
     * Returns true if the total moves a team can make are zero.
     * Returns false otherwise.
     * */
    public boolean logicStalemateCheckmate(TeamColor teamColor){
        for (int r = 1; r < 9; r++){
            for (int c = 1; c < 9; c++){
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> pieceMoves = validMoves(position);
                        int movesSize = pieceMoves.size();
                        if (movesSize > 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }
    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
