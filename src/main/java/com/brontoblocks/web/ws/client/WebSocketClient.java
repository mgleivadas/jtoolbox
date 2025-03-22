package com.brontoblocks.web.ws.client;


import com.brontoblocks.common.ConnectionId;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * This class exists so that we differentiate from the exposed interface (with a better name)
 * and also be able to extend the functionality given.
 */
public final class WebSocketClient {

    static WebSocketClient create(WebSocket webSocket) {
        return new WebSocketClient(webSocket);
    }

    private WebSocketClient(WebSocket webSocket) {
        this.connectionId = ConnectionId.createNew();
        this.webSocket = webSocket;
    }

    public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
        return webSocket.sendText(data, last);
    }

    public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
        return webSocket.sendBinary(data, last);
    }

    public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
        return webSocket.sendPing(message);
    }

    public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
        return webSocket.sendPong(message);
    }

    public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
        return webSocket.sendClose(statusCode, reason);
    }

    public void request(long n) {
        webSocket.request(n);
    }

    public String getSubProtocol() {
        return webSocket.getSubprotocol();
    }

    public boolean isConnected() {
        return isInputClosed() || isOutputClosed();
    }

    public boolean isOutputClosed() {
        return webSocket.isOutputClosed();
    }

    public boolean isInputClosed() {
        return webSocket.isInputClosed();
    }

    public void abort() {
        webSocket.abort();
    }

    public ConnectionId getConnectionId() {
        return connectionId;
    }

    private final ConnectionId connectionId;
    private final WebSocket webSocket;
}
