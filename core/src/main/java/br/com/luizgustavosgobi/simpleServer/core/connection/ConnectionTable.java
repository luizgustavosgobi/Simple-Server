package br.com.luizgustavosgobi.simpleServer.core.connection;

import java.net.InetSocketAddress;
import java.util.Set;

public interface ConnectionTable {

    void add(Client s);
    Client get(InetSocketAddress address);
    void remove(Client s);
    Set<Client> getConnections();
}
