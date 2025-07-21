package test;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;

import java.nio.channels.SocketChannel;

public class TestConnectionHandler implements ConnectionHandler {

    @Override
    public void onAccept(SocketChannel client) {
        System.out.println("Client " + client.socket().getInetAddress().getHostAddress());
    }

    @Override
    public void onRead(SocketChannel client, byte[] data) {
        System.out.println("Leu!" + new String(data));
    }

    @Override
    public void onClose(SocketChannel client) {

    }
}
