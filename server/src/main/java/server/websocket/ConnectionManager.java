package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectionManager {
    public final Map<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void addConnection(int gameID, String visitorName, WsContext context) {
        var connection = new Connection(visitorName, context);
        connections.computeIfAbsent(gameID, k -> new CopyOnWriteArraySet<>()).add(connection);
    }

    public void removeConnection(int gameID, String visitorName) {
        var gameSet = connections.get(gameID);
        if (gameSet != null) {
            gameSet.removeIf(conn -> conn.visitorName.equals(visitorName));
            if (gameSet.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, WsContext excludeCtx, ServerMessage notification) {
        var connectionsInGame = connections.get(gameID);

        if (connectionsInGame != null) {
            for (var connection : connectionsInGame) {
                if (connection.context.session.isOpen()) {
                    if (!connection.context.equals(excludeCtx)) {
                        connection.send(new Gson().toJson(notification));
                    }
                }
            }
        }
    }
}
