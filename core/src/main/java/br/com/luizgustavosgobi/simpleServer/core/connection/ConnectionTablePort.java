package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Set;

public interface ConnectionTablePort {

    void add(SocketChannel s);
    void remove(SocketChannel s);
    void disconnect(SocketChannel s) throws IOException;
    Set<SocketChannel> getConnections();
}
