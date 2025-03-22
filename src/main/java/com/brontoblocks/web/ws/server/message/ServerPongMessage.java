package com.brontoblocks.web.ws.server.message;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.PONG;

public final class ServerPongMessage extends ServerWebSocketControlMessage {

    public static ServerPongMessage create(String message) {
        return new ServerPongMessage(message);
    }

    private ServerPongMessage(String message) {
        super(PONG);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
