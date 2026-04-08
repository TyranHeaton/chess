package websocket;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import ui.NotificationHandler;


public class WebSocketCommunicator extends Endpoint {
	private Session session;
	private NotificationHandler notificationHandler;

	public WebSocketCommunicator(String url, NotificationHandler handler) throws Exception {
		this.notificationHandler = handler;
		// TODO: Implement method
	}

	public void connect(String authToken, int gameID) throws Exception {
		// TODO: Implement method
	}

	@Override
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		// Intentionally empty until websocket message handlers are added.
	}

}
