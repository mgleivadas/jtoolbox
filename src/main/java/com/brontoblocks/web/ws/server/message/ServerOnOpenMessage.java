package com.brontoblocks.web.ws.server.message;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.NEW_CONNECTION;

public final class ServerOnOpenMessage extends ServerWebSocketControlMessage {

    public static ServerOnOpenMessage create(WebSocket connection, ClientHandshake handshake) {
        return new ServerOnOpenMessage(connection, handshake);
    }

    private ServerOnOpenMessage(WebSocket clientConnection, ClientHandshake handshake) {
        super(NEW_CONNECTION);
        this.clientConnection = clientConnection;
        this.handshake = handshake;
    }

    public WebSocket getClientConnection() {
        return clientConnection;
    }

    public ClientHandshake getHandshake() {
        return handshake;
    }

    private final WebSocket clientConnection;
    private final ClientHandshake handshake;
}
