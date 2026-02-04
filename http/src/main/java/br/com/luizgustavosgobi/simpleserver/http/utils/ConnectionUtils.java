package br.com.luizgustavosgobi.simpleServer.http.utils;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTablePort;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ConnectionUtils {

    public void sendAndDisconnect(SocketChannel client, ConnectionTablePort connectionTablePort, ResponseEntity<?> response) {
        try {
            SocketStream.write(client, response.getBytes());
        } catch (IOException e) {
            Logger.Error(ConnectionUtils.class, "Failed to send data: " + e.getMessage());
        } finally {
            try {
                connectionTablePort.disconnect(client);
            } catch (IOException e) {
                Logger.Error(this, "Error disconnecting inactive connection: " + e.getMessage());
            }
        }
    }
}
