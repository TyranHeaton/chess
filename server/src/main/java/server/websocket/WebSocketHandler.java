package server.websocket;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;

public class WebSocketHandler {

    public WebSocketHandler(WsConfig ws){
        ws.onMessage(this::onMessage);
    }

    private void onMessage(WsMessageContext ctx){
        System.out.println("Received message: " + ctx.message());
    }
}
