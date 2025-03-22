package com.brontoblocks.web.ws.server.message;

import org.java_websocket.WebSocket;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.ERROR;

public final class ServerOnErrorMessage extends ServerWebSocketControlMessage {

    public static ServerOnErrorMessage create(WebSocket clientConnection, Exception exception) {
        return new ServerOnErrorMessage(clientConnection, exception);
    }

    public WebSocket getClientConnection() {
        return clientConnection;
    }

    public Exception getException() {
        return exception;
    }

    private ServerOnErrorMessage(WebSocket clientConnection, Exception exception) {
        super(ERROR);
        this.clientConnection = clientConnection;
        this.exception = exception;
    }

    private final WebSocket clientConnection;
    private final Exception exception;
}
