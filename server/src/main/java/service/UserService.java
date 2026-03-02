package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDatabase;
    private final AuthDAO authDatabase;

    public UserService(UserDAO userDatabase, AuthDAO authDatabase) {
        this.userDatabase = userDatabase;
        this.authDatabase = authDatabase;
    }

    public AuthData register(UserData newUserData) throws DataAccessException {
        if (newUserData.username() == null || newUserData.password() == null || newUserData.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (userDatabase.get(newUserData.username()) != null) {
            throw new DataAccessException("Error: username already exists");
        }
        userDatabase.insert(newUserData);
        return login(newUserData.username(), newUserData.password());
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDatabase.get(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized"); // 401
        }
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authDatabase.insert(authData);

        return authData;
    }

    public void logout(String token) throws DataAccessException {
        if (authDatabase.get(token) == null) {
            throw new DataAccessException("Error: unauthorized"); // 401
        }
        authDatabase.delete(token);
    }

}
