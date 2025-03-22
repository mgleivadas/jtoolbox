package com.brontoblocks.web.ws.server.message;

import org.java_websocket.WebSocket;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.BINARY_PAYLOAD;

public final class ServerOnBinaryPayload extends ServerWebSocketControlMessage {

    public static ServerOnBinaryPayload create(WebSocket clientConnection, byte[] payload) {
        return new ServerOnBinaryPayload(clientConnection, payload);
    }

    private ServerOnBinaryPayload(WebSocket clientConnection, byte[] payload) {
        super(BINARY_PAYLOAD);
        this.clientConnection = clientConnection;
        this.payload = payload;
    }

    public WebSocket getClientConnection() {
        return clientConnection;
    }

    public byte[] getPayload() {
        return payload;
    }

    private final WebSocket clientConnection;
    private final byte[] payload;
}
