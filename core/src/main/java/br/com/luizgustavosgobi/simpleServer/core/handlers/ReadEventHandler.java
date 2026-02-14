package br.com.luizgustavosgobi.simpleServer.core.handlers;

import br.com.luizgustavosgobi.simpleServer.core.ThreadManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.core.io.SocketStream;

public class ReadEventHandler {
    private final ConnectionHandler connectionHandler;
    private final ThreadManager threadManager;
    private final SocketStream socketStream;
    private final FilterChainProxy filterChainProxy;

    public ReadEventHandler(ConnectionHandler connectionHandler, ThreadManager threadManager, SocketStream socketStream, FilterChainProxy filterChainProxy) {
        this.connectionHandler = connectionHandler;
        this.threadManager = threadManager;
        this.socketStream = socketStream;
        this.filterChainProxy = filterChainProxy;
    }

    public void handle(Client client) throws Exception {
        Object data = socketStream.read(client.getChannel(), client.getDataPipelineContext());

        threadManager.submitToIO(() -> {
            try {
                if (filterChainProxy != null) {
                    filterChainProxy.doFilter(client, data, context -> {
                        if (context.getResponse() != null) client.write(context.getResponse());
                        else connectionHandler.onRead(client, context.getRequest());
                    });
                }
                else connectionHandler.onRead(client, data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
