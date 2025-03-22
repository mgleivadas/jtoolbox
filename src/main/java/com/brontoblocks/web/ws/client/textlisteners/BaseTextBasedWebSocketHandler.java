package com.brontoblocks.web.ws.client.textlisteners;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public abstract class BaseTextBasedWebSocketHandler implements WebSocket.Listener {

    protected BaseTextBasedWebSocketHandler() {
        this.messageBuffer = new ArrayList<>();
    }

    private final List<String> messageBuffer;

    protected abstract void onOpening(WebSocket webSocket);

    protected abstract void onClosing(int statusCode, String reason);

    protected abstract void onTextDataReceived(String data);

    protected abstract void onTextDataReceived(List<String> data);

    protected abstract void onErrorReceived(Throwable error);

    protected void onBinaryReceived(ByteBuffer data, boolean last) {
        try {
            onErrorReceived(new RuntimeException("Received binary data."));
        } catch (Throwable ex) {
            // Do nothing if exception occurs during reporting exception
        }
    }

    protected abstract void onPing(ByteBuffer data);

    protected abstract void onPong(ByteBuffer data);

    @Override
    public void onOpen(WebSocket webSocket) {
        onOpening(webSocket);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

        if (last) {
            if (messageBuffer.isEmpty()) {
                onTextDataReceived(data.toString());
            } else {
                messageBuffer.add(data.toString());
                onTextDataReceived(List.copyOf(messageBuffer));
                messageBuffer.clear();
            }
        } else {
            messageBuffer.add(data.toString());
        }

        WebSocket.Listener.super.onText(webSocket, data, last);
        return null;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        onBinaryReceived(data, last);
        WebSocket.Listener.super.onBinary(webSocket, data, last);
        return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        onPing(message);
        WebSocket.Listener.super.onPing(webSocket, message);
        return null;
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        onPong(message);
        WebSocket.Listener.super.onPong(webSocket, message);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        onClosing(statusCode, reason);
        WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        onErrorReceived(error);
        WebSocket.Listener.super.onError(webSocket, error);
    }
}
