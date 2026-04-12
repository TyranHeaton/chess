package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.NotificationHandler;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class WebSocketCommunicator {
	private Session session;
	private final NotificationHandler notificationHandler;
	private CountDownLatch connectLatch;
	private final WebSocketContainer container;

    public WebSocketCommunicator(NotificationHandler notificationHandler) throws Exception {
		this.notificationHandler = notificationHandler;
		this.container = ContainerProvider.getWebSocketContainer();
	}

	@OnMessage
	public void onMessage(String message) {
		ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

		switch (serverMessage.getServerMessageType()) {
			case LOAD_GAME -> serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
			case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
			case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
		}

		notificationHandler.notify(serverMessage);
	}

	public void connect(String authToken, int gameID) throws Exception {
		this.connectLatch = new CountDownLatch(1);

		try {
			URI uri = new URI("ws://localhost:8080/ws");
			this.session = container.connectToServer(this, uri);

			if (!connectLatch.await(5, TimeUnit.SECONDS)) {
				throw new Exception("WebSocket connection timed out!");
			}

			UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
			this.send(connectCommand);

		} catch (IOException e) {
			throw new Exception("Failed to send connect command: " + e.getMessage());
		}
	}

	public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
		var command = new MakeMoveCommand(authToken, gameID, move);
		this.send(command);
	}

	public void leaveGame(String authToken, int gameID) throws IOException {
		var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
		this.send(command);
	}

	public void resign(String authToken, int gameID) throws IOException {
		var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
		this.send(command);
	}

	public void send(UserGameCommand command) throws IOException {
		if (this.session == null || !this.session.isOpen()) {
			throw new IOException("WebSocket is not connected.");
		}
		String json = new Gson().toJson(command);
		this.session.getBasicRemote().sendText(json);
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		if (this.connectLatch != null) {
			this.connectLatch.countDown();
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		this.session = null;
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.err.println("WebSocket error: " + throwable.getMessage());
	}



}
