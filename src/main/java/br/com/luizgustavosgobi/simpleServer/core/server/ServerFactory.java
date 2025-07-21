package br.com.luizgustavosgobi.simpleServer.core.server;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.io.IOException;

public class ServerFactory {
    private static int serverCount = 0;

    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connHandler, ApplicationContext context) throws IOException {
        ConfigurationManager configurationManager = new ConfigurationManager();
        Logger logger = new Logger(mainClass.getSimpleName());

        context
                .register(logger)
                .register("basePackage", mainClass.getPackage().getName());

        int portN = port != null ? port : configurationManager.getInt("server." + (serverCount == 0 ? "" : (serverCount + 1) + ".") + "port", 3000);
        boolean blocking = configurationManager.getBoolean("server.blocking", false);

        Server server = new Server(portN, blocking, connHandler);
        context.register(server);

        serverCount++;
        return server;
    }

    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connHandler) throws IOException {
        return create(mainClass, port, connHandler, ContextHolder.create());
    }

    public static Server create(Class<?> mainClass, ConnectionHandler connHandler) throws IOException {
        return create(mainClass, null, connHandler, ContextHolder.create());
    }
}
