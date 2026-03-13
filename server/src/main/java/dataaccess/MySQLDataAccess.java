package dataaccess;

import exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLDataAccess{
    String createTableUser = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255) NOT_NULL, email VARCHAR(255)) NOT_NULL";
    String createTableAuth = "CREATE TABLE IF NOT EXISTS auth (authToken VARCHAR(255) PRIMARY KEY, username VARCHAR(255) NOT_NULL, FOREIGN KEY (username) REFERENCES users(username))";
    String createTableGame = "CREATE TABLE IF NOT EXISTS games (gameID INT PRIMARY KEY AUTO_INCREMENT, whiteUsername VARCHAR(255), blackUsername VARCHAR(255), gameName VARCHAR(255) NOT NULL, jsonText LONGTEXT NOT NULL)";
    String[] createTableStatements = {createTableUser, createTableAuth, createTableGame};


    public void executeInsert(Object data) throws DataAccessException {

    }


    public void delete(String id) throws DataAccessException {

    }


    public void clear() throws DataAccessException {

    }

    public Object executeQuery(String id) throws DataAccessException {
        return null;
    }

    protected int executeUpdate(String statementSQL) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
             try (PreparedStatement preparedStatement = conn.prepareStatement(statementSQL)) {
                 return preparedStatement.executeUpdate();
             }
        }
             catch (SQLException ex) {
            throw new DataAccessException("Not able to execute update");
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createTableStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create tables", ex);
        }
    }


}
