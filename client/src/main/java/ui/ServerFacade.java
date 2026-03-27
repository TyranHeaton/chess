package ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(int port) {
        this.serverURL = "http://localhost:" + port;
    }

    // Pre-login public methods

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }

    public AuthData register(String username, String password, String email) throws Exception {
        String path = "/user";
        Map<String, String> requestBody = Map.of("username", username, "password", password, "email", email);
        return makeRequest("POST", path, null, requestBody, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        String path = "/session";
        Map<String, String> requestBody = Map.of("username", username, "password", password);
        return makeRequest("POST", path, null, requestBody, AuthData.class);
    }


    // Post-login public methods

    public void logout(String authToken) throws Exception {
        String path = "/session";
        makeRequest("DELETE", path, authToken, null, null);
    }

    public int createGame(String authToken, String gameName) throws Exception {
        String path = "/game";
        record CreateResponse(int gameID) {}
        Map<String, String> requestBody = Map.of("gameName", gameName);
        var response = makeRequest("POST", path, authToken, requestBody, CreateResponse.class);
        return response.gameID();
    }

    public GameData[] listGames(String authToken) throws Exception {
        String path = "/game";
        record ListResponse(GameData[] games) {}
        var response = makeRequest("GET", path, authToken, null, ListResponse.class);
        return response.games();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        String path = "/game";
        Map<String, Object> requestBody = Map.of("gameID", gameID, "playerColor", playerColor);
        makeRequest("PUT", path, authToken, requestBody, null);
    }

    // Private helper methods

    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> response) throws Exception {
        try {
            HttpURLConnection httpConnection = getHttpURLConnection(method, path);
            httpConnection.setRequestMethod(method);

            if (authToken != null) {
                httpConnection.setRequestProperty("Authorization", authToken);
            }

            if (request != null) {
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setDoOutput(true);
                var gson = new GsonBuilder().serializeNulls().create();
                String requestData = gson.toJson(request);
                try (var outputStream = httpConnection.getOutputStream()) {
                    outputStream.write(requestData.getBytes(StandardCharsets.UTF_8));
                }
            }

            httpConnection.connect();

            // Throws an exception if the server returns any status code other than '200 OK'
            if (httpConnection.getResponseCode() < 200 || httpConnection.getResponseCode() >= 300) {
                throw new Exception("Error: " + httpConnection.getResponseMessage());
            }

            // Deserializes the JSON response body into a Java object and returns it.
            return readBody(httpConnection, response);

        // Catches any error that occurred during the request and rethrows it with the original error message
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }

    }

    private HttpURLConnection getHttpURLConnection(String method, String path) throws URISyntaxException, IOException {
        URL url = new URI(serverURL + path).toURL(); // Creates a URL object by combining the base address and the specific endpoint path.
        return (HttpURLConnection) url.openConnection();
    }

    private <T> T readBody(HttpURLConnection httpConnection, Class<T> response) throws IOException {
        if (response == null) { return null; }
        try (InputStream responseBody = httpConnection.getInputStream()) {
            // Acts as a bridge that decodes the raw byte stream from the server into readable text characters.
            InputStreamReader reader = new InputStreamReader(responseBody);
            return new Gson().fromJson(reader, response); // Uses Gson to deserialize the response into a Java object
        }
    }
}
