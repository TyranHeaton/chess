package service;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import dataaccess.MemoryUserDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDatabase;
    private final AuthDAO authDatabase;

    public UserService(MemoryUserDAO userDatabase, MemoryAuthDAO authDatabase) {
        this.userDatabase = userDatabase;
        this.authDatabase = authDatabase;
    }

    public AuthData register(UserData newUserData) throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        if (newUserData.username() == null || newUserData.password() == null || newUserData.email() == null) {
            throw new BadRequestException();
        }
        if (userDatabase.get(newUserData.username()) != null) { //getUser
            throw new AlreadyTakenException();
        }

        userDatabase.insert(newUserData); // createUser
        return login(newUserData.username(), newUserData.password());
    }

    public AuthData login(String username, String password) throws DataAccessException, BadRequestException, UnauthorizedException {
        UserData user = userDatabase.get(username); // getUser
        if (username == null || password == null) {
            throw new BadRequestException();
        }
        if (user == null || !user.password().equals(password)) {
            throw new UnauthorizedException(); // 401
        }
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authDatabase.insert(authData); // createAuth

        return authData;
    }

    public void logout(String token) throws DataAccessException, UnauthorizedException {
        if (authDatabase.get(token) == null) {
            throw new UnauthorizedException(); // 401
        }
        authDatabase.delete(token); // deleteAuth
    }

}
