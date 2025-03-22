package com.brontoblocks.web.ws.client.message;

import java.net.http.WebSocket;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.WS_OPEN;

public final class ClientOnOpenMessage extends ClientWebSocketControlMessage {

    public static ClientOnOpenMessage create(WebSocket webSocket) {
        return new ClientOnOpenMessage(webSocket);
    }

    private ClientOnOpenMessage(WebSocket webSocket) {
        super(WS_OPEN);
        this.webSocket = webSocket;
    }

    private final WebSocket webSocket;
}
