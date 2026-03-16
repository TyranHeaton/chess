package service;

import dataaccess.*;
import exceptions.DataAccessException;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;

    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();

    }

}

