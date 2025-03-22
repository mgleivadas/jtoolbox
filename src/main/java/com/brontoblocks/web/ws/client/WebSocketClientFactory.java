package com.brontoblocks.web.ws.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class WebSocketClientFactory {

    public static final int DEFAULT_CONNECTION_TIMEOUT_IN_SECONDS = 10;

    public static WebSocketClient defaultWebSocketClient(String uri, WebSocket.Listener listener) {

        return defaultWebSocketClient(uri, listener, DEFAULT_CONNECTION_TIMEOUT_IN_SECONDS);
    }

    public static WebSocketClient defaultWebSocketClient(String uri, WebSocket.Listener listener, int connectionTimeoutInSeconds) {

        try {
            return WebSocketClient.create(
                    HttpClient.newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create(uri), listener)
                    .get(connectionTimeoutInSeconds, SECONDS));
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }
}
