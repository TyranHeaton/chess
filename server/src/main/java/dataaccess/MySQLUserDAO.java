package dataaccess;

import exceptions.DataAccessException;
import model.UserData;


public class MySQLUserDAO extends MySQLDataAccess implements UserDAO {

    @Override
    public void insert(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sql, user.username(), user.password(), user.email());
    }

    @Override
    public void delete(String username) throws DataAccessException {
        String sql = "DELETE FROM users WHERE username = ?";
        executeUpdate(sql, username);
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE users";
        executeUpdate(sql);

    }
    @Override
    public UserData get(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";
        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
            return null;
        }, username);
    }
}

