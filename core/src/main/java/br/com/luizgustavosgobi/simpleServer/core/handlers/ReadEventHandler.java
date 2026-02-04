package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadEventHandler {
    private final ConnectionHandlerPort connectionHandlerPort;
    private final ThreadManager threadManager;

    public ReadEventHandler(ConnectionHandlerPort connectionHandlerPort, ThreadManager threadManager) {
        this.connectionHandlerPort = connectionHandlerPort;
        this.threadManager = threadManager;
    }

    public boolean handle(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        try {
            byte[] data = SocketStream.read(client);

            threadManager.submitToIO(() -> connectionHandlerPort.onRead(client, data));
        } catch (IOException e) {
            Logger.Debug(ReadEventHandler.class, "Client closed connection: " + client.socket().getRemoteSocketAddress());
            return false;
        }

        return true;
    }
}
