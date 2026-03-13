package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccess<GameData> {
    void insert(GameData gameData) throws DataAccessException;

    void delete(String gameID) throws DataAccessException;

    void clear() throws DataAccessException;

    GameData get(String gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData updatedGame) throws DataAccessException;
}
