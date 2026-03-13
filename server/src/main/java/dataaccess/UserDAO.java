package dataaccess;

import exceptions.DataAccessException;
import model.UserData;

public interface UserDAO extends DataAccess<UserData> {
    void insert(UserData user) throws DataAccessException;

    void delete(String username) throws DataAccessException;

    void clear() throws DataAccessException;

    UserData get(String username) throws DataAccessException;
}
