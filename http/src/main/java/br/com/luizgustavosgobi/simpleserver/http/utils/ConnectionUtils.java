package br.com.luizgustavosgobi.simpleServer.http.utils;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

import java.io.IOException;

public class ConnectionUtils {

    public void sendAndDisconnect(Client client, ConnectionTable connectionTable, ResponseEntity<?> response) {
        try {
            client.write(response.getBytes());
            connectionTable.disconnect(client);
        } catch (IOException e) {
            Logger.Error(ConnectionUtils.class, "Failed to send data: " + e.getMessage());
        }
    }
}
