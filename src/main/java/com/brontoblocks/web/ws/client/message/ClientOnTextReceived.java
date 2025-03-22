package com.brontoblocks.web.ws.client.message;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.SINGLE_TEXT;

public final class ClientOnTextReceived extends ClientWebSocketControlMessage {

    public static ClientOnTextReceived create(String message) {
        return new ClientOnTextReceived(message);
    }

    private ClientOnTextReceived(String message) {
        super(SINGLE_TEXT);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;
}
