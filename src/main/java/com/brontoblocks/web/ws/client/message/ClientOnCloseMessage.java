package com.brontoblocks.web.ws.client.message;

import static com.brontoblocks.web.ws.client.message.ClientWebSocketMessageType.WS_CLOSE;

public final class ClientOnCloseMessage extends ClientWebSocketControlMessage {

    public static ClientOnCloseMessage create(int statusCode, String reason) {
        return new ClientOnCloseMessage(statusCode, reason);
    }

    private ClientOnCloseMessage(int statusCode, String reason) {
        super(WS_CLOSE);
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }

    private final int statusCode;
    private final String reason;
}
