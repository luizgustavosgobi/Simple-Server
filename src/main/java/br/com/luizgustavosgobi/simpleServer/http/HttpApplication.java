package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.core.server.Server;
import br.com.luizgustavosgobi.simpleServer.core.server.ServerFactory;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Arrays;
import java.util.logging.Level;

public class HttpApplication {
    public static Server run(Class<?> mainClass, Integer port) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        ApplicationContext context = ContextHolder.create();
        context
                .register(new Router(), new ObjectMapper())
                .register(Validator.class, Validation.buildDefaultValidatorFactory().getValidator());

        try {
            Server server = ServerFactory.create(mainClass, port, new HttpConnectionHandler(), context);

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
