package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTablePort;
import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class CloseEventHandler {
    private final ConnectionTablePort connectionTablePort;
    private final ConnectionHandlerPort connectionHandlerPort;
    private final ThreadManager threadManager;

    public CloseEventHandler(ConnectionTablePort connectionTablePort, ConnectionHandlerPort connectionHandlerPort, ThreadManager threadManager) {
        this.connectionTablePort = connectionTablePort;
        this.connectionHandlerPort = connectionHandlerPort;
        this.threadManager = threadManager;
    }

    public void handle(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        threadManager.submitToIO(() -> connectionHandlerPort.onClose(client));

        if (key.isValid())
            key.cancel();

        try {
            connectionTablePort.disconnect(client);
        } catch (IOException e) {
            Logger.Error("Error to disconnect an client: " + client.socket().getRemoteSocketAddress());
        }
    }
}
