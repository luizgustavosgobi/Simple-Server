package test.A;

import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.AutoWired;
import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.Bean;
import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.Configuration;

@Configuration
public class As {

    @AutoWired
    private HelloService helloService;

    @Bean
    public Config sayHello() {
        return new Config();
    }
}
