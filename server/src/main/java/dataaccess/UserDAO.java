package dataaccess;

import model.UserData;

public class UserDAO implements DataAccess<UserData> {
    @Override
    public void insert(UserData data) throws DataAccessException {

    }

    @Override
    public void delete(String id) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public UserData get(String id) throws DataAccessException {
        return null;
    }

}
