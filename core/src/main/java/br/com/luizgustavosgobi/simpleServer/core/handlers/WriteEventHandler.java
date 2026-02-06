package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.io.SelectorScheduler;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

public class WriteEventHandler {
    private final ThreadManager threadManager;
    private final ConnectionHandler connectionHandler;
    private final SocketStream socketStream;
    private final SelectorScheduler scheduler;

    public WriteEventHandler(ConnectionHandler connHandler, ThreadManager threadManager, SocketStream socketStream, SelectorScheduler scheduler) {
        this.connectionHandler = connHandler;
        this.threadManager = threadManager;
        this.socketStream = socketStream;
        this.scheduler = scheduler;
    }

    public void handle(Client client) {
        try {
            while (client.hasWrites()) {
                Object data = client.pollWrite();
                if (data == null) break;

                socketStream.write(client.getChannel(), client.getDataPipelineContext(), data);
            }

            if (!client.hasWrites()) scheduler.cancelWriteSchedule(client);

            threadManager.submitToIO(() -> connectionHandler.onWrite(client));
        } catch (Exception e) {
            Logger.Error("Error writing to client " + client.getAddress() + ": " + e.getMessage());
        }
    }
}
