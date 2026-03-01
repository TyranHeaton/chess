package dataaccess;

import model.AuthData;
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
        int ID = Integer.parseInt(gameID);
        games.remove(ID);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public GameData get(String gameID) throws DataAccessException {
        int ID = Integer.parseInt(gameID);
        return games.get(ID);
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return games.values();
    }

    public void updateGame(GameData updatedGame) throws DataAccessException {
        games.put(updatedGame.gameID(), updatedGame);
    }
}
