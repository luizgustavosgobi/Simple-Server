package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;

public class CloseEventHandler {
    private final ConnectionTable connectionTable;
    private final ConnectionHandler connectionHandler;
    private final ThreadManager threadManager;

    public CloseEventHandler(ConnectionTable connectionTable, ConnectionHandler connectionHandler, ThreadManager threadManager) {
        this.connectionTable = connectionTable;
        this.connectionHandler = connectionHandler;
        this.threadManager = threadManager;
    }

    public void handle(Client client) {
        threadManager.submitToIO(() -> connectionHandler.onClose(client));

        try {
            connectionTable.remove(client);
            client.close();
        } catch (IOException e) {
            Logger.Error("Error to disconnect an client: " + client.getAddress());
        }
    }
}
