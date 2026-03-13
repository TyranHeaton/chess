package service;

import dataaccess.*;
import exceptions.DataAccessException;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(MemoryUserDAO userDAO, MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

}

