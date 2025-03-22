package com.brontoblocks.web.ws.server.message;

public enum ServerWebSocketMessageType {
    SERVER_START,
    CONNECTION_CLOSE,
    TEXT_PAYLOAD,
    BINARY_PAYLOAD,
    ERROR,
    NEW_CONNECTION,
    PING,
    PONG
}
