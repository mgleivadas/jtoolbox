package com.brontoblocks.web.ws.client.message;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.ERROR;

public final class ClientOnErrorMessage extends ClientWebSocketControlMessage {

    public static ClientOnErrorMessage create(String errorMessage) {
        return new ClientOnErrorMessage(errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private ClientOnErrorMessage(String errorMessage) {
        super(ERROR);
        this.errorMessage = errorMessage;
    }

    private final String errorMessage;
}
