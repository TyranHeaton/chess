package dataaccess;

import exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLDataAccess{


    public void executeInsert(Object data) throws DataAccessException {

    }


    public void delete(String id) throws DataAccessException {

    }


    public void clear() throws DataAccessException {

    }

    @FunctionalInterface
    public interface ResponseMapper<T> {
        T map(ResultSet result) throws SQLException;
    }

    @FunctionalInterface
    public interface StatementAction<T> {
        T apply(PreparedStatement ps) throws SQLException;
    }

    private <T> T execute(String sql, StatementAction<T> action, Object ... params) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                for (int i = 1; i <= params.length; i++) {
                    ps.setObject(i, params[i - 1]);
                }
                return action.apply(ps);
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("SQL Error: " + ex.getMessage());
        }
    }

    protected <T> T executeQuery(String sql, ResponseMapper<T> mapper, Object ... params) throws DataAccessException {
        return execute(sql, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return mapper.map(rs);
            }
        }, params);
    }

    protected void executeUpdate(String statementSQL, Object ... params) throws DataAccessException {
        execute(statementSQL, ps -> {
            ps.executeUpdate();
            return null;
        }, params);
    }

    public void configureDatabase() throws DataAccessException {
        String createTableUser = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL)";
        String createTableAuth = "CREATE TABLE IF NOT EXISTS auth (authToken VARCHAR(255) PRIMARY KEY, username VARCHAR(255) NOT NULL, FOREIGN KEY (username) REFERENCES users(username))";
        String createTableGame = "CREATE TABLE IF NOT EXISTS games (gameID INT PRIMARY KEY AUTO_INCREMENT, whiteUsername VARCHAR(255), blackUsername VARCHAR(255), gameName VARCHAR(255) NOT NULL, jsonText LONGTEXT NOT NULL)";
        String[] createTableStatements = {createTableUser, createTableAuth, createTableGame};

        for (String sql : createTableStatements) {
            executeUpdate(sql);
        }
    }
}
