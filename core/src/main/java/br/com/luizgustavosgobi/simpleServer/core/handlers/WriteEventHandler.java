package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.nio.channels.SelectionKey;

public class WriteEventHandler {
    private final ThreadManager threadManager;
    private final ConnectionHandler connectionHandler;
    private final SocketStream socketStream;

    public WriteEventHandler(ConnectionHandler connHandler, ThreadManager threadManager, SocketStream socketStream) {
        this.connectionHandler = connHandler;
        this.threadManager = threadManager;
        this.socketStream = socketStream;
    }

    public void handle(Client client) {
        try {
            while (client.hasWrites()) {
                Object data = client.pollWrite();
                if (data == null) break;

                socketStream.write(client.getChannel(), client.getDataPipelineContext(), data);
            }

            if (!client.hasWrites()) {
                client.removeKeyInterestIn(SelectionKey.OP_WRITE);

                Object attribute = client.getAttribute("closeAfterWrite");
                if (attribute != null && (boolean) attribute) client.shouldClose(true);
            }

            threadManager.submitToIO(() -> connectionHandler.onWrite(client));
        } catch (Exception e) {
            Logger.Error("Error writing to client " + client.getAddress() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
