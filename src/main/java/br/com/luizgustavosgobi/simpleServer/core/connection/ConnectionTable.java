package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.nio.channels.SocketChannel;
import java.util.Set;

public interface ConnectionTable {

    void add(SocketChannel s);
    void remove(SocketChannel s);
    void disconnect(SocketChannel s);
    Set<SocketChannel> getConnections();
}
