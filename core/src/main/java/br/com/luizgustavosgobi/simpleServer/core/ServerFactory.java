package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.configuration.ServerConfiguration;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.connection.ClientConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.*;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipeline;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;

public class ServerFactory {
    private static final ConfigurationManager configurationManager = ConfigurationManager.getOrCreate();
    private static final ServerRegistry serverRegistry = ServerRegistry.getInstance();


    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connectionHandler, ConnectionTable table, BeanRegistry externalBeanRegistry, DataPipeline dataPipeline) throws IOException {
        Logger logger = new Logger(mainClass.getSimpleName());

        int serverIndex = serverRegistry.getNextServerIndex();
        ServerConfiguration config = ServerConfiguration.resolve(mainClass, port, serverIndex, configurationManager);

        ApplicationContextBootstrap bootstrap = new ApplicationContextBootstrap(mainClass, logger, externalBeanRegistry);
        BeanRegistry applicationContext = bootstrap.getBeanRegistry();

        ThreadManager threadManager = new ThreadManager();
        ConnectionTable connectionTable = table;

        applicationContext.register(new BeanDefinition("THREAD_MANAGER", ThreadManager.class, BeanScope.SINGLETON, false, threadManager));
        applicationContext.register(new BeanDefinition("CONNECTION_TABLE", ConnectionTable.class, BeanScope.SINGLETON, false, connectionTable));

        Server server = new Server(
                config.getPort(),
                config.isBlocking(),
                connectionTable,
                connectionHandler,
                threadManager,
                applicationContext,
                logger,
                dataPipeline
        );

        applicationContext.register(new BeanDefinition("SERVER", Server.class, BeanScope.SINGLETON, false, server));

        serverRegistry.registerServer(mainClass, server);

        Logger.Info(ServerFactory.class, "Server created: " + config);

        return server;
    }

    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connectionHandler, DataPipeline dataPipeline) throws IOException {
        return create(mainClass, port, connectionHandler, new ClientConnectionTable(), new ApplicationContext(), dataPipeline);
    }

    public static Server create(Class<?> mainClass, ConnectionHandler connectionHandler) throws IOException {
        return create(mainClass, null, connectionHandler, new DataPipeline());
    }
}
