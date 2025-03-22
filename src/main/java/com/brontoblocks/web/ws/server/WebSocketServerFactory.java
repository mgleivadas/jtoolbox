package com.brontoblocks.web.ws.server;

import com.brontoblocks.web.ws.server.message.ServerWebSocketControlMessage;

import java.util.concurrent.LinkedBlockingQueue;

public final class WebSocketServerFactory {

    public static BaseWebSocketServer defaultWebSocketServer(
            String ip,
            int port,
            LinkedBlockingQueue<ServerWebSocketControlMessage> queue) {

        return BaseWebSocketServer.create(ip, port, queue);
    }
}
