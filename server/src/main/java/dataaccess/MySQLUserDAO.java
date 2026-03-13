package dataaccess;

import exceptions.DataAccessException;
import model.UserData;


public class MySQLUserDAO extends MySQLDataAccess implements UserDAO {

    @Override
    public void insert(UserData user) throws DataAccessException {

    }

    @Override
    public void delete(String username) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE users");

    }
    @Override
    public UserData get(String username) throws DataAccessException {
        return null;
    }
}

