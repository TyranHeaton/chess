package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO extends MySQLDataAccess implements GameDAO {

    private String serializeGame(ChessGame game){
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String gameData){
        return new Gson().fromJson(gameData, ChessGame.class);
    }

    @Override
    public void insert(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO games (game_id, whiteUsername, blackUsername, gameName, jsonText) VALUES (?, ?, ?, ?, ?)";
        String jsonGame = serializeGame(gameData.game());
        executeUpdate(sql, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), jsonGame);
    }

    @Override
    public GameData get(String ID) throws DataAccessException {
        int gameID = Integer.parseInt(ID);
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonText FROM games WHERE gameID = ?";
        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        deserializeGame(rs.getString("jsonText"))
                );
            }
            return null;
        }, gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, jsonText FROM games";
        var games = new ArrayList<GameData>();
        return executeQuery(sql, rs -> {
            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        deserializeGame(rs.getString("jsonText"))
                ));
            }
            return games;
        });
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, jsonText = ? WHERE gameID = ?";
        String jsonGame = serializeGame(updatedGame.game());
        executeUpdate(sql, updatedGame.whiteUsername(), updatedGame.blackUsername(), updatedGame.gameName(), jsonGame, updatedGame.gameID());
    }

    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE games";
        executeUpdate(sql);
    }
}
