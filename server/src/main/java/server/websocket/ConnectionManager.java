package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String visitorName, WsContext context) {
        var connection = new Connection(visitorName, context);
        connections.put(visitorName, connection);
    }

    public void removeConnection(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, ServerMessage notification) {
        var cleanUp = new ArrayList<Connection>();

        for (var connection : connections.values()) {
            if (connection.context.session.isOpen()) {
                if (!connection.visitorName.equals(excludeVisitorName)) {
                    connection.send(new Gson().toJson(notification));
                }
            } else {
                cleanUp.add(connection);
            }
        }
        for (var badConnection : cleanUp) {
            connections.remove(badConnection.visitorName);
        }
    }
}
