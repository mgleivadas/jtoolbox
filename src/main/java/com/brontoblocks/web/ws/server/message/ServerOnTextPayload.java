package com.brontoblocks.web.ws.server.message;

import org.java_websocket.WebSocket;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.TEXT_PAYLOAD;

public final class ServerOnTextPayload extends ServerWebSocketControlMessage {

    public static ServerOnTextPayload create(WebSocket clientConnection, String payload) {
        return new ServerOnTextPayload(clientConnection, payload);
    }

    private ServerOnTextPayload(WebSocket clientConnection, String payload) {
        super(TEXT_PAYLOAD);
        this.clientConnection = clientConnection;
        this.payload = payload;
    }

    public WebSocket getClientConnection() {
        return clientConnection;
    }

    public String getPayload() {
        return payload;
    }

    private final WebSocket clientConnection;
    private final String payload;
}
