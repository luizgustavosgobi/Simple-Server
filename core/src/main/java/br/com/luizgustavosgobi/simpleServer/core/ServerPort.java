package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;

import java.io.Closeable;
import java.io.IOException;

public interface ServerPort extends Closeable {

    void start();

    @Override
    void close() throws IOException;

    boolean isRunning();

    int getPort();

    ThreadManager getThreadManager();

    ConnectionHandler getConnectionHandler();

    ConnectionTable getConnectionTable();

    BeanRegistry getContext();
}
