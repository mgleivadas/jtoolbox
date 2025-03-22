package com.brontoblocks.web.ws.client.textlisteners;

import com.brontoblocks.web.ws.client.message.ClientOnCloseMessage;
import com.brontoblocks.web.ws.client.message.ClientOnErrorMessage;
import com.brontoblocks.web.ws.client.message.ClientOnMultiTextReceived;
import com.brontoblocks.web.ws.client.message.ClientOnOpenMessage;
import com.brontoblocks.web.ws.client.message.ClientOnTextReceived;
import com.brontoblocks.web.ws.client.message.ClientPingMessage;
import com.brontoblocks.web.ws.client.message.ClientPongMessage;
import com.brontoblocks.web.ws.client.message.ClientWebSocketControlMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public final class TextBasedWebSocketMessageHandler extends BaseTextBasedWebSocketHandler {

    public TextBasedWebSocketMessageHandler(CountDownLatch latch, BlockingQueue<ClientWebSocketControlMessage> queue) {
        this.queue = queue;
        this.latch = latch;
    }

    @Override
    protected void onOpening(WebSocket webSocket) {
        queue.add(ClientOnOpenMessage.create(webSocket));
        latch.countDown();
    }

    @Override
    protected void onClosing(int statusCode, String reason) {
        queue.add(ClientOnCloseMessage.create(statusCode, reason));
    }

    @Override
    protected void onTextDataReceived(String data) {
        queue.add(ClientOnTextReceived.create(data));
    }

    @Override
    protected void onTextDataReceived(List<String> data) {
        queue.add(ClientOnMultiTextReceived.create(data));
    }

    @Override
    protected void onErrorReceived(Throwable error) {
        queue.add(ClientOnErrorMessage.create(ExceptionUtils.getStackTrace(error)));
    }

    @Override
    protected void onPing(ByteBuffer data) {
        queue.add(ClientPingMessage.create(StandardCharsets.UTF_8.decode(data).toString()));
    }

    @Override
    protected void onPong(ByteBuffer data) {
        queue.add(ClientPongMessage.create(StandardCharsets.UTF_8.decode(data).toString()));
    }

    private final BlockingQueue<ClientWebSocketControlMessage> queue;
    private final CountDownLatch latch;
}
