package dataaccess;

import exceptions.DataAccessException;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO implements DataAccess<UserData> {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void insert(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public void delete(String username) throws DataAccessException {
        users.remove(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public UserData get(String username) throws DataAccessException {
        return users.get(username);
    }

}
