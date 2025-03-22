package com.brontoblocks.web.ws.client.message;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.PING;

public final class ClientPingMessage extends ClientWebSocketControlMessage {

    public static ClientPingMessage create(String message) {
        return new ClientPingMessage(message);
    }

    private ClientPingMessage(String message) {
        super(PING);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
