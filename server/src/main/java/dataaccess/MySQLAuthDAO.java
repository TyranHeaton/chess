package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;

public class MySQLAuthDAO extends MySQLDataAccess implements AuthDAO {

    @Override
    public void insert(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(sql, auth.authToken(), auth.username());
    }

    @Override
    public AuthData get(String token) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";

        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return new AuthData(
                        rs.getString("authToken"),
                        rs.getString("username")
                );
            }
            return null;
        }, token);
    }

    public void delete(String token) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(sql, token);
    }

    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE auth";
        executeUpdate(sql);
    }
}
