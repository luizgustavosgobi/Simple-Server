package br.com.luizgustavosgobi.simpleServer.http.utils;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.utils.DataUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ConnectionUtils {
    private static final ConnectionTable connectionTable = ContextHolder.getContext().get(ConnectionTable.class);

    public static void sendAndDisconnect(SocketChannel client, ResponseEntity<?> response) {
        try { DataUtils.writeData(client, response.getBytes()); }
        catch (IOException e) { Logger.Error(ConnectionUtils.class, "Failed to send data: " + e.getMessage()); }
        finally { connectionTable.disconnect(client); }
    }
}
