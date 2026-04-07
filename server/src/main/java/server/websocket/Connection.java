package server.websocket;

import io.javalin.websocket.WsContext;

public class Connection {
    public String visitorName;
    public WsContext context;

    public Connection(String visitorName, WsContext context) {
        this.visitorName = visitorName;
        this.context = context;
    }

    public void send(String message) {
        context.send(message);
    }
}
