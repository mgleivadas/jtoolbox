package com.brontoblocks.web.ws.server.message;

import org.java_websocket.WebSocket;

import static com.brontoblocks.web.ws.server.message.ServerWebSocketMessageType.CONNECTION_CLOSE;

public final class ServerOnCloseMessage extends ServerWebSocketControlMessage {

    public static ServerOnCloseMessage create(WebSocket conn, int code, String reason, boolean remote) {
        return new ServerOnCloseMessage(conn, code, reason, remote);
    }

    private ServerOnCloseMessage(WebSocket conn, int code, String reason, boolean remote) {
        super(CONNECTION_CLOSE);
        this.conn = conn;
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public WebSocket getConn() {
        return conn;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }

    private final WebSocket conn;
    private final int code;
    private final String reason;
    private final boolean remote;
}
