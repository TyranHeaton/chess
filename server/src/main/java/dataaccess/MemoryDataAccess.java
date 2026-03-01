package dataaccess;

public class MemoryDataAccess<T> implements DataAccess<T>{

    @Override
    public void insert(T data) throws DataAccessException {

    }
    @Override
    public void delete(String id) throws DataAccessException {

    }
    @Override
    public void clear() throws DataAccessException {

    }
    @Override
    public T get(String id) throws DataAccessException {
        return null;
    }
}
