package ui;

import model.AuthData;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade (int port) {
        this.serverURL = "http://localhost:" + port;
    }

    // Pre-login public methods

    public AuthData register(String username, String password, String email) {
        String path = "/user";
        // TODO: Implement makeRequest helper method.
        return null;
    }


    // Post-login public methods


    // Private helper methods
    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> response) throws Exception {
        try {
            URL url = new URI(serverURL + path).toURL();
            //TODO: Complete this method
        }
        catch (Exception exception){
            throw new Exception(exception.getMessage());

        }
        return null;
    }

}
