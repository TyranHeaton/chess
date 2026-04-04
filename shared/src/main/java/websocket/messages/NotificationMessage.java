package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String notification;

    public NotificationMessage(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }
}
