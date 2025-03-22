package com.brontoblocks.web.ws.client.message;

import java.util.List;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.MULTI_TEXT;

public final class ClientOnMultiTextReceived extends ClientWebSocketControlMessage {

    public static ClientOnMultiTextReceived create(List<String> messages) {
        return new ClientOnMultiTextReceived(messages);
    }

    private ClientOnMultiTextReceived(List<String> messages) {
        super(MULTI_TEXT);

        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    private final List<String> messages;
}
