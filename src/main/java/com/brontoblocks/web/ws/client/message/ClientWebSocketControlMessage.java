package com.brontoblocks.web.ws.client.message;

import java.util.Objects;

public abstract class ClientWebSocketControlMessage {

    protected ClientWebSocketControlMessage(ClientWebSocketMessageType type) {
        this.type = type;
    }

    public ClientWebSocketMessageType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientWebSocketControlMessage that = (ClientWebSocketControlMessage) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    private final ClientWebSocketMessageType type;
}
