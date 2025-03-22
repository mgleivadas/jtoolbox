package com.brontoblocks.web.ws.client.message;

public enum ClientWebSocketMessageType {
    ERROR,
    WS_OPEN,
    WS_CLOSE,
    SINGLE_TEXT,
    MULTI_TEXT,
    PING,
    PONG
}
