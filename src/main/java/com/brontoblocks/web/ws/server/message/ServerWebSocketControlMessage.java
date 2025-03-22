package com.brontoblocks.web.ws.server.message;

import java.util.Objects;

public abstract class ServerWebSocketControlMessage {

    protected ServerWebSocketControlMessage(ServerWebSocketMessageType type) {
        this.type = type;
    }

    public ServerWebSocketMessageType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerWebSocketControlMessage that = (ServerWebSocketControlMessage) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    private final ServerWebSocketMessageType type;
}
