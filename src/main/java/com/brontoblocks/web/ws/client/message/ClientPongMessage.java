package com.brontoblocks.web.ws.client.message;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.PONG;

public final class ClientPongMessage extends ClientWebSocketControlMessage {

    public static ClientPongMessage create(String message) {
        return new ClientPongMessage(message);
    }

    private ClientPongMessage(String message) {
        super(PONG);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
