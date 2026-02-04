package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTablePort;
import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import jdk.net.ExtendedSocketOptions;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler {
    private final ConnectionTablePort connectionTablePort;
    private final ConnectionHandlerPort connectionHandlerPort;
    private final ThreadManager threadManager;
    private final boolean isBlockingIo;

    private final ConfigurationManager configManager;

    private final boolean KEEPALIVE;
    private final int KEEPALIVE_IDLE_TIME;
    private final int KEEPALIVE_REFRESH_INTERVAL;
    private final int KEEPALIVE_TRIES;

    public AcceptEventHandler(ConnectionTablePort connectionTablePort, ConnectionHandlerPort connectionHandlerPort, ThreadManager threadManager, boolean isBlockingIo) {
        this.connectionTablePort = connectionTablePort;
        this.connectionHandlerPort = connectionHandlerPort;
        this.threadManager = threadManager;
        this.isBlockingIo = isBlockingIo;
        this.configManager = ConfigurationManager.getOrCreate();

        this.KEEPALIVE = configManager.getBoolean("connection.keepalive", true);
        this.KEEPALIVE_IDLE_TIME = configManager.getInt("connection.keepalive.idle", 30);
        this.KEEPALIVE_REFRESH_INTERVAL = configManager.getInt("connection.keepalive.interval", 10);
        this.KEEPALIVE_TRIES = configManager.getInt("connection.keepalive.tries", 3);
    }

    public void handle(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();

        if (client != null) {
            client.configureBlocking(isBlockingIo);

            client.setOption(StandardSocketOptions.SO_KEEPALIVE, KEEPALIVE);
            client.setOption(ExtendedSocketOptions.TCP_KEEPIDLE, KEEPALIVE_IDLE_TIME);
            client.setOption(ExtendedSocketOptions.TCP_KEEPINTERVAL, KEEPALIVE_REFRESH_INTERVAL);
            client.setOption(ExtendedSocketOptions.TCP_KEEPCOUNT, KEEPALIVE_TRIES);

            client.register(selector, SelectionKey.OP_READ);
            connectionTablePort.add(client);

            threadManager.submitToIO(() -> connectionHandlerPort.onAccept(client));
        }
    }
}
