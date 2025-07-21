package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.nio.channels.SocketChannel;

public interface ConnectionHandler {
    void onAccept(SocketChannel client);
    void onRead(SocketChannel client, byte[] data);
    void onClose(SocketChannel client);
}
