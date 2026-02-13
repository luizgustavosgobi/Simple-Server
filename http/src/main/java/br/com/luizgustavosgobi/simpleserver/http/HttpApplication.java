package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.Server;
import br.com.luizgustavosgobi.simpleServer.core.ServerFactory;
import br.com.luizgustavosgobi.simpleServer.core.connection.ClientConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
import br.com.luizgustavosgobi.simpleServer.core.converter.ByteToStringCodec;
import br.com.luizgustavosgobi.simpleServer.core.converter.DataPipeline;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Arrays;
import java.util.logging.Level;

public class HttpApplication {
    public static Server run(Class<?> mainClass, Integer port) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        Router router = new Router();
        ClientConnectionTable connTable = new ClientConnectionTable();
        ApplicationContext context = new ApplicationContext();

        DataPipeline pipeline =  new DataPipeline();
        pipeline.addLast(new ByteToStringCodec())
                .addLast(new StringToHttpCoded());

        HttpConnectionHandler handler = new HttpConnectionHandler(router, connTable);

        context.register(new BeanDefinition("ROUTER", Router.class, BeanScope.SINGLETON, router));
        context.register(new BeanDefinition("OBJECT_MAPPER", ObjectMapper.class, BeanScope.SINGLETON, new ObjectMapper()));
        context.register(new BeanDefinition("OBJECT_VALIDATOR", Validator.class, BeanScope.SINGLETON, Validation.buildDefaultValidatorFactory().getValidator()));

        try {
            Server server = ServerFactory.create(mainClass, port, handler, connTable, context, pipeline);
            server.start();
            return server;
        } catch (Exception e) {
            Logger.Fatal(HttpApplication.class, "Erro ao iniciar o servidor: " + Arrays.toString(e.getStackTrace()));
            System.exit(-1);
            return null;
        }
    }

    public static Server run(Class<?> mainClass) {
        return run(mainClass, null);
    }
}
