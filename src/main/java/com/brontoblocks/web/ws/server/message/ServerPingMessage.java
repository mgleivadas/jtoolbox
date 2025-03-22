package com.brontoblocks.web.ws.server.message;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.PING;

public final class ServerPingMessage extends ServerWebSocketControlMessage {

    public static ServerPingMessage create(String message) {
        return new ServerPingMessage(message);
    }

    private ServerPingMessage(String message) {
        super(PING);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
