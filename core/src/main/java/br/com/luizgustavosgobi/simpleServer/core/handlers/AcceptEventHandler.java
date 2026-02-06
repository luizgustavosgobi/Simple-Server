package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.io.WriteScheduler;
import jdk.net.ExtendedSocketOptions;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler {
    private final ConnectionTable connectionTable;
    private final ConnectionHandler connectionHandler;
    private final ThreadManager threadManager;
    private final boolean isBlockingIo;

    private final Selector selector;
    private final WriteScheduler writeScheduler;

    private final ConfigurationManager configManager;

    private final boolean KEEPALIVE;
    private final int KEEPALIVE_IDLE_TIME;
    private final int KEEPALIVE_REFRESH_INTERVAL;
    private final int KEEPALIVE_TRIES;

    public AcceptEventHandler(ConnectionTable connectionTable, ConnectionHandler connectionHandler,
                            ThreadManager threadManager, Selector selector, boolean isBlockingIo, WriteScheduler writeScheduler) {
        this.connectionTable = connectionTable;
        this.connectionHandler = connectionHandler;
        this.threadManager = threadManager;
        this.isBlockingIo = isBlockingIo;
        this.configManager = ConfigurationManager.getOrCreate();
        this.selector = selector;
        this.writeScheduler = writeScheduler;

        this.KEEPALIVE = configManager.getBoolean("connection.keepalive", true);
        this.KEEPALIVE_IDLE_TIME = configManager.getInt("connection.keepalive.idle", 30);
        this.KEEPALIVE_REFRESH_INTERVAL = configManager.getInt("connection.keepalive.interval", 10);
        this.KEEPALIVE_TRIES = configManager.getInt("connection.keepalive.tries", 3);
    }

    public void handle(Client client) throws IOException {
        SocketChannel clientChannel = client.getChannel();

        clientChannel.configureBlocking(false);

        clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, KEEPALIVE);
        clientChannel.setOption(ExtendedSocketOptions.TCP_KEEPIDLE, KEEPALIVE_IDLE_TIME);
        clientChannel.setOption(ExtendedSocketOptions.TCP_KEEPINTERVAL, KEEPALIVE_REFRESH_INTERVAL);
        clientChannel.setOption(ExtendedSocketOptions.TCP_KEEPCOUNT, KEEPALIVE_TRIES);

        SelectionKey key = clientChannel.register(selector, SelectionKey.OP_READ);
        client.setSelectionKey(key);

        connectionTable.add(client);

        threadManager.submitToIO(() -> connectionHandler.onAccept(client));
    }
}
