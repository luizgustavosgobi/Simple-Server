package br.com.luizgustavosgobi.simpleServer.core.server;

import br.com.luizgustavosgobi.simpleServer.core.connection.DefaultConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.beans.AnnotationManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.core.executors.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.utils.DataUtils;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Getter
public class Server {
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ThreadManager threadManager;
    private final ConnectionTable connectionTable;
    private final ConnectionHandler connectionHandler;

    private final ApplicationContext context;

    // --------------------------------------------
    // -----------     Constructors     -----------
    // --------------------------------------------

    public Server(int port, boolean blocking, ConnectionTable connTable, ConnectionHandler connHandler) throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(blocking);

        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.threadManager = new ThreadManager();
        this.connectionTable = connTable;
        this.connectionHandler = connHandler;

        this.context = ContextHolder.getOrCreate();
        context.register(connTable, threadManager);
    }

    public Server(int port, boolean blocking, ConnectionHandler connHandler) throws IOException {
        this(port, blocking, new DefaultConnectionTable(), connHandler);
    }

    public Server(int port, ConnectionHandler connHandler) throws IOException {
        this(port, false, new DefaultConnectionTable(), connHandler);
    }

    //
    //
    //

    public void start() {
        AnnotationManager annotationManager = new AnnotationManager();
        annotationManager.autoDiscoverAndRegisterProcessors();
        annotationManager.scanAndProcess((String) context.get("basePackage"));

        threadManager.submitToIO(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (this.selector.select() == 0) continue;

                    for (SelectionKey key : selector.selectedKeys()) {
                        if (key.isAcceptable()) {
                            SocketChannel client = serverChannel.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            threadManager.submitToIO(() -> connectionHandler.onAccept(client));
                        }

                        if (key.isReadable()) {
                            byte[] data = DataUtils.readData((SocketChannel) key.channel());
                            threadManager.submitToIO(() -> connectionHandler.onRead((SocketChannel) key.channel(), data));
                        }

                        if (!key.isValid()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            threadManager.submitToIO(() -> connectionHandler.onClose(client));
                            key.cancel();
                            connectionTable.disconnect(client);
                        }
                    }

                    selector.selectedKeys().clear();
                } catch (IOException e) {
                    Logger.Error("Error while handling connection: " + e.getMessage());
                }
            }

            Logger.Info("Server stopped handling connections");
        });

        Logger.Info("Server started on 0.0.0.0:" +  serverChannel.socket().getLocalPort());
    }

    public void close() throws IOException {
        serverChannel.close();
        selector.close();
        threadManager.shutdown();

        Logger.Info(this, "Server closed");
    }
}
