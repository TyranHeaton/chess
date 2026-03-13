package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void insert(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public void delete(String token) throws DataAccessException {
        auths.remove(token);
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public AuthData get(String token) throws DataAccessException {
        return auths.get(token);
    }
}
