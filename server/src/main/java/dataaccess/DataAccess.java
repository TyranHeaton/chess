package dataaccess;

import exceptions.DataAccessException;

public interface DataAccess<T> {
    void insert(T data) throws DataAccessException;
    void delete(String id) throws DataAccessException;
    void clear() throws DataAccessException;
    T get(String id) throws DataAccessException;
}
