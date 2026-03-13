package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO extends DataAccess<AuthData> {
    void insert(AuthData auth) throws DataAccessException;

    void delete(String token) throws DataAccessException;

    void clear() throws DataAccessException;

    AuthData get(String token) throws DataAccessException;
}
