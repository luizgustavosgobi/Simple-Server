package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

public class ReadEventHandler {
    private final ConnectionHandler connectionHandler;
    private final ThreadManager threadManager;
    private final SocketStream socketStream;

    public ReadEventHandler(ConnectionHandler connectionHandler, ThreadManager threadManager, SocketStream socketStream) {
        this.connectionHandler = connectionHandler;
        this.threadManager = threadManager;
        this.socketStream = socketStream;
    }

    public boolean handle(Client client) {
        try {
            Object data = socketStream.read(client.getChannel(), client.getDataPipelineContext());

            threadManager.submitToIO(() -> connectionHandler.onRead(client, data));
        } catch (Exception e) {
            Logger.Debug("Client closed connection: " + client.getAddress());
            return false;
        }

        return true;
    }
}
