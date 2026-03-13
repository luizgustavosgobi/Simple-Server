package br.com.luizgustavosgobi.simpleServer.core;

import br.com.luizgustavosgobi.simpleServer.core.configuration.ConfigurationManager;
import br.com.luizgustavosgobi.simpleServer.core.configuration.ServerConfiguration;
import br.com.luizgustavosgobi.simpleServer.core.connection.ClientConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.*;
import br.com.luizgustavosgobi.simpleServer.core.converter.ConverterPipelineProxy;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.IOException;

public class ServerFactory {
    private static final ConfigurationManager configurationManager = ConfigurationManager.getOrCreate();
    private static final ServerRegistry serverRegistry = ServerRegistry.getInstance();


    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connectionHandler, ConnectionTable table,
                                BeanRegistry externalBeanRegistry, ConverterPipelineProxy converterPipelineProxy) throws IOException {
        Logger logger = new Logger(mainClass.getSimpleName());

        int serverIndex = serverRegistry.getNextServerIndex();
        ServerConfiguration config = ServerConfiguration.resolve(mainClass, port, serverIndex, configurationManager);

        ApplicationContextBootstrap bootstrap = new ApplicationContextBootstrap(mainClass, logger, externalBeanRegistry);
        BeanRegistry applicationContext = bootstrap.getBeanRegistry();

        ThreadManager threadManager = new ThreadManager();

        applicationContext.register(new BeanDefinition("THREAD_MANAGER", ThreadManager.class, BeanScope.SINGLETON, threadManager));
        applicationContext.register(new BeanDefinition("CONNECTION_TABLE", ConnectionTable.class, BeanScope.SINGLETON, table));

        if (converterPipelineProxy == null) converterPipelineProxy = applicationContext.getInstance(ConverterPipelineProxy.class);
        FilterChainProxy filterChainProxy = applicationContext.getInstance(FilterChainProxy.class);

        Server server = new Server(
                config.getPort(),
                config.isBlocking(),
                table,
                connectionHandler,
                threadManager,
                applicationContext,
                logger,
                converterPipelineProxy,
                filterChainProxy
        );

        applicationContext.register(new BeanDefinition("SERVER", Server.class, BeanScope.SINGLETON, server));

        serverRegistry.registerServer(mainClass, server);

        Logger.Info(ServerFactory.class, "Server created: " + config);

        return server;
    }

    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connectionHandler, ConverterPipelineProxy converterPipelineProxy) throws IOException {
        return create(mainClass, port, connectionHandler, new ClientConnectionTable(), new ApplicationContext(), converterPipelineProxy);
    }

    public static Server create(Class<?> mainClass, Integer port, ConnectionHandler connectionHandler) throws IOException {
        return create(mainClass, port, connectionHandler, null);
    }
}
