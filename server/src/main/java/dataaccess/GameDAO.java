package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameDAO implements DataAccess<GameData>{
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void insert(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void delete(String gameID) throws DataAccessException {
        int id = Integer.parseInt(gameID);
        games.remove(id);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public GameData get(String gameID) throws DataAccessException {
        int id = Integer.parseInt(gameID);
        return games.get(id);
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return games.values();
    }

    public void updateGame(GameData updatedGame) throws DataAccessException {
        games.put(updatedGame.gameID(), updatedGame);
    }
}
