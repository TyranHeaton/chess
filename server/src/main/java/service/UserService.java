package service;
import dataaccess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDAO userDatabase;
    private final AuthDAO authDatabase;

    public UserService(UserDAO userDatabase, AuthDAO authDatabase) {
        this.userDatabase = userDatabase;
        this.authDatabase = authDatabase;
    }

    public AuthData register(UserData newUserData) throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        System.out.println("SERVICE: Checking if user exists: " + newUserData.username());
        if (newUserData.username() == null || newUserData.password() == null || newUserData.email() == null) {
            throw new BadRequestException();
        }
        if (userDatabase.get(newUserData.username()) != null) { //getUser
            System.out.println("SERVICE: User already exists, skipping insert.");
            throw new AlreadyTakenException();
        }
        System.out.println("SERVICE: Inserting user into Database...");
        String hashedPassword = BCrypt.hashpw(newUserData.password(), BCrypt.gensalt());
        userDatabase.insert(new UserData(newUserData.username(), hashedPassword, newUserData.email())); // createUser
        return login(newUserData.username(), newUserData.password());
    }

    public AuthData login(String username, String password) throws DataAccessException, BadRequestException, UnauthorizedException {
        UserData user = userDatabase.get(username); // getUser
        if (username == null || password == null) {
            throw new BadRequestException();
        }
        if (user == null || !BCrypt.checkpw(password, user.password())) {
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
