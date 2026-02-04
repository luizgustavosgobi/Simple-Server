package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandlerPort;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTablePort;
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

    ConnectionHandlerPort getConnectionHandler();

    ConnectionTablePort getConnectionTable();

    BeanRegistry getContext();
}
