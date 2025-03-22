package com.brontoblocks.web.ws.server;

import com.brontoblocks.utils.Await;
import com.brontoblocks.web.ws.server.message.ServerOnBinaryPayload;
import com.brontoblocks.web.ws.server.message.ServerOnCloseMessage;
import com.brontoblocks.web.ws.server.message.ServerOnErrorMessage;
import com.brontoblocks.web.ws.server.message.ServerOnOpenMessage;
import com.brontoblocks.web.ws.server.message.ServerOnTextPayload;
import com.brontoblocks.web.ws.server.message.ServerWebSocketControlMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.InetAddress.getAllByName;

public class BaseWebSocketServer extends WebSocketServer {

    public static int MAX_PROPER_CLOSE_TIMEOUT_IN_MILLIS = 10_000;
    public static int MAX_PENDING_CONNECTIONS = 100;

    public static BaseWebSocketServer create(
            String ip, int port, LinkedBlockingQueue<ServerWebSocketControlMessage> queue) {

        var server = new BaseWebSocketServer(ip, port, queue);
        server.setMaxPendingConnections(MAX_PENDING_CONNECTIONS);
        server.setTcpNoDelay(true);
        server.start();

        if (Await.upToSeconds(5, server::hasServerStarted)) {
            return server;
        } else {
            server.stop();
            throw new RuntimeException("Could not start websocket server");
        }
    }

    private BaseWebSocketServer(
            String ip,
            int port,
            LinkedBlockingQueue<ServerWebSocketControlMessage> queue) {
        this(convertToInetSocketAddress(ip, port), queue);
    }

    private BaseWebSocketServer(InetSocketAddress inetSocketAddress,
                                LinkedBlockingQueue<ServerWebSocketControlMessage> queue) {

        super(inetSocketAddress);
        this.queue = queue;
        this.isServerStarted = new AtomicBoolean(false);
        this.serverGeneralError = Optional.empty();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        queue.add(ServerOnOpenMessage.create(conn, handshake));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        queue.add(ServerOnCloseMessage.create(conn, code, reason, remote));
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        queue.add(ServerOnBinaryPayload.create(conn, message.array()));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        queue.add(ServerOnTextPayload.create(conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn == null) {
            startUpResult(false, ex);
        } else {
            queue.add(ServerOnErrorMessage.create(conn, ex));
        }
    }

    public boolean hasServerStarted() {
        return isServerStarted.get();
    }

    private void startUpResult(boolean serverStarted, Exception ex) {
        serverGeneralError = Optional.ofNullable(ex);
        isServerStarted.set(serverStarted);
    }

    @Override
    public void onStart() {
        startUpResult(true, null);
    }

    @Override
    public void stop() {
        try {
            this.stop(MAX_PROPER_CLOSE_TIMEOUT_IN_MILLIS);
            isServerStarted.set(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Optional<Exception> getServerGeneralError() {
        return serverGeneralError;
    }

    private static InetSocketAddress convertToInetSocketAddress(String ip, int port) {
        try {
            return new InetSocketAddress(getAllByName(ip)[0], port);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final LinkedBlockingQueue<ServerWebSocketControlMessage> queue;
    private final AtomicBoolean isServerStarted;
    private volatile Optional<Exception> serverGeneralError;
}
