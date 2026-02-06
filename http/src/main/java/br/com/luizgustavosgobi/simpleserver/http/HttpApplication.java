package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.Server;
import br.com.luizgustavosgobi.simpleServer.core.ServerFactory;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
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

        BeanRegistry context = new ApplicationContext();
        Router router = new Router();

        context.register(new BeanDefinition("ROUTER", Router.class, BeanScope.SINGLETON, router));
        context.register(new BeanDefinition("OBJECT_MAPPER", ObjectMapper.class, BeanScope.SINGLETON, new ObjectMapper()));
        context.register(new BeanDefinition("OBJECT_VALIDATOR", Validator.class, BeanScope.SINGLETON, Validation.buildDefaultValidatorFactory().getValidator()));

        try {
            HttpConnectionHandler handler = new HttpConnectionHandler(router);

            Server server = ServerFactory.create(mainClass, port, handler, context);

            handler.setConnectionTable(server.getConnectionTable());

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
