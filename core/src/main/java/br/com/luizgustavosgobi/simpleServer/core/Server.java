package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTablePort;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.handlers.AcceptEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.handlers.CloseEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.handlers.ReadEventHandler;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server implements ServerPort {
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ThreadManager threadManager;
    private final ConnectionTablePort connectionTablePort;
    private final ConnectionHandlerPort connectionHandlerPort;
    private final BeanRegistry context;
    private final Logger logger;
    private final boolean isBlockingIo;
    private final int port;


    private final AcceptEventHandler acceptHandler;
    private final ReadEventHandler readHandler;
    private final CloseEventHandler closeHandler;

    private volatile boolean running = false;

    // --------------------------------------------
    // -----------     Constructors     -----------
    // --------------------------------------------

    public Server(int port, boolean blocking, ConnectionTablePort connectionTablePort, ConnectionHandlerPort connectionHandlerPort,
                  ThreadManager threadManager, BeanRegistry context, Logger logger) throws IOException {

        this.port = port;
        this.isBlockingIo = blocking;
        this.threadManager = threadManager;
        this.connectionTablePort = connectionTablePort;
        this.connectionHandlerPort = connectionHandlerPort;
        this.context = context;
        this.logger = logger;

        this.serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(blocking);

        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.acceptHandler = new AcceptEventHandler(connectionTablePort, connectionHandlerPort, threadManager, isBlockingIo);
        this.readHandler = new ReadEventHandler(connectionHandlerPort, threadManager);
        this.closeHandler = new CloseEventHandler(connectionTablePort, connectionHandlerPort, threadManager);
    }

    public Server(int port, boolean blocking, ConnectionTablePort connectionTablePort, ConnectionHandlerPort connectionHandlerPort,
                  BeanRegistry context, Logger logger) throws IOException {

        this(port, blocking, connectionTablePort, connectionHandlerPort, new ThreadManager(), context, logger);
    }

    public Server(int port, boolean blocking, ConnectionHandlerPort connHandler) throws IOException {
        this(port, blocking, new ConnectionTable(), connHandler, new ThreadManager(), null, null);
    }

    public Server(int port, ConnectionHandlerPort connHandler) throws IOException {
        this(port, false, connHandler);
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
                            if (key.isAcceptable()) acceptHandler.handle(key, selector);

                            else if (key.isReadable()) {
                                boolean shouldKeepOpen = readHandler.handle(key);
                                if (!shouldKeepOpen) key.cancel();
                            }

                            if (!key.isValid()) closeHandler.handle(key);
                        } catch (IOException e) {
                            logger.error("Error handling event: " + e.getMessage());

                            if (key.isValid()) {
                                closeHandler.handle(key);
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
    public ConnectionTablePort getConnectionTable() {
        return connectionTablePort;
    }

    @Override
    public ConnectionHandlerPort getConnectionHandler() {
        return connectionHandlerPort;
    }

    @Override
    public BeanRegistry getContext() {
        return context;
    }

    public boolean isBlockingIo() {
        return isBlockingIo;
    }
}
