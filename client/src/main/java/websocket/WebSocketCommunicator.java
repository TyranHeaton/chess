package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.NotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;


public class WebSocketCommunicator extends Endpoint {
	private Session session;

    public WebSocketCommunicator(String url, NotificationHandler notificationHandler) throws Exception {
        URI uri = new URI(url + "/ws");
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		this.session = container.connectToServer(this, uri);

		this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {

            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
            }

            notificationHandler.notify(serverMessage);
        });
	}

	public void connect(String authToken, int gameID) throws Exception {
		try {
			UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
			this.send(connectCommand);

		} catch (IOException e) {
			throw new Exception("Failed to send connect command: " + e.getMessage());
		}
	}

	public void send(UserGameCommand command) throws IOException {
		String json = new Gson().toJson(command);
		this.session.getBasicRemote().sendText(json);
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		this.session = session;
	}

}
