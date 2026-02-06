package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipeline;
import br.com.luizgustavosgobi.simpleServer.core.handlers.AcceptEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.handlers.CloseEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.handlers.ReadEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.handlers.WriteEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.io.SelectorScheduler;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server implements ServerPort {
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ThreadManager threadManager;
    private final ConnectionTable connectionTable;
    private final ConnectionHandler connectionHandler;
    private final BeanRegistry context;
    private final Logger logger;
    private final boolean isBlockingIo;
    private final int port;


    private final AcceptEventHandler acceptHandler;
    private final ReadEventHandler readHandler;
    private final WriteEventHandler writeHandler;
    private final CloseEventHandler closeHandler;

    private final SocketStream socketStream;
    private final DataPipeline dataPipeline;

    private final SelectorScheduler selectorScheduler;

    private volatile boolean running = false;


    public Server(int port, boolean blocking, ConnectionTable connectionTable, ConnectionHandler connectionHandler,
                  ThreadManager threadManager, BeanRegistry context, Logger logger, DataPipeline dataPipeline) throws IOException {

        this.port = port;
        this.isBlockingIo = blocking;
        this.threadManager = threadManager;
        this.connectionTable = connectionTable;
        this.connectionHandler = connectionHandler;
        this.context = context;
        this.logger = logger;
        this.dataPipeline = dataPipeline;
        this.socketStream = new SocketStream(dataPipeline);

        this.serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(blocking);

        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.selectorScheduler = new SelectorScheduler(selector);

        this.acceptHandler = new AcceptEventHandler(connectionTable, connectionHandler, threadManager, selector, isBlockingIo, selectorScheduler);
        this.readHandler = new ReadEventHandler(connectionHandler, threadManager, socketStream);
        this.writeHandler = new WriteEventHandler(connectionHandler, threadManager, socketStream, selectorScheduler);
        this.closeHandler = new CloseEventHandler(connectionTable, connectionHandler, threadManager);
    }

    public Server(int port, boolean blocking, ConnectionTable connectionTable, ConnectionHandler connectionHandler,
                  BeanRegistry context, Logger logger, DataPipeline dataPipeline) throws IOException {

        this(port, blocking, connectionTable, connectionHandler, new ThreadManager(), context, logger, dataPipeline);
    }


    @Override
    public void start() {
        if (running) {
            logger.info("Server is already running");
            return;
        }

        running = true;

        threadManager.submitToIO(() -> {
            while (!Thread.currentThread().isInterrupted() && running) {
                try {
                    if (this.selector.select() == 0) continue;

                    for (SelectionKey key : selector.selectedKeys()) {
                        try {
                            if (key.isAcceptable()) {
                                ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverChannel.accept();

                                Client client = new Client(clientChannel, selectorScheduler);

                                acceptHandler.handle(client);
                                continue;
                            }

                            SocketChannel channel = (SocketChannel) key.channel();
                            Client client = connectionTable.get((InetSocketAddress) channel.getRemoteAddress());
                            if (client == null)
                                throw new IllegalArgumentException("Event disparate on an unregistered client");

                            if (key.isReadable()) {
                                boolean shouldKeepOpen = readHandler.handle(client);
                                if (!shouldKeepOpen) key.cancel();
                            }

                            else if (key.isWritable()) writeHandler.handle(client);

                            if (!key.isValid()) closeHandler.handle(client);
                        } catch (Exception e) {
                            logger.error("Error handling event: " + e.getMessage());

                            if (key.isValid()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                Client client = connectionTable.get((InetSocketAddress) channel.getRemoteAddress());

                                closeHandler.handle(client);
                            }
                        }
                    }

                    selector.selectedKeys().clear();
                } catch (IOException e) {
                    logger.error("Error while handling connection: " + e.getMessage());
                }
            }

            logger.info("Server stopped handling connections");
        });

        logger.info("Server started on 0.0.0.0:" + port);
    }

    @Override
    public void close() throws IOException {
        running = false;
        serverChannel.close();
        selector.close();
        threadManager.shutdown();

        logger.info("Server closed");
    }

    @Override
    public boolean isRunning() {
        return running && serverChannel.isOpen();
    }

    @Override
    public int getPort() {
        return port;
    }

    public Selector getSelector() {
        return selector;
    }

    public ServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    @Override
    public ThreadManager getThreadManager() {
        return threadManager;
    }

    @Override
    public ConnectionTable getConnectionTable() {
        return connectionTable;
    }

    @Override
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public BeanRegistry getContext() {
        return context;
    }

    public boolean isBlockingIo() {
        return isBlockingIo;
    }
}
