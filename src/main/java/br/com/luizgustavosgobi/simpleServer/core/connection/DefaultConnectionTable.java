package br.com.luizgustavosgobi.simpleServer.core.connection;

import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

public class DefaultConnectionTable implements ConnectionTable {
    private final HashSet<SocketChannel> connections;

    public DefaultConnectionTable() {
        this.connections = new HashSet<>();
    }


    @Override
    public void add(SocketChannel s) {
        connections.add(s);
    }

    @Override
    public void remove(SocketChannel s) {
        connections.remove(s);
    }

    @Override
    public void disconnect(SocketChannel s) {
        try { s.close(); }
        catch (IOException e) {
            Logger.Error(this, "Error While disconnecting a connection: " + e.getMessage());
        }

        connections.remove(s);
    }

    @Override
    public Set<SocketChannel> getConnections() {
        return connections;
    }
}
