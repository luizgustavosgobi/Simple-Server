package test.A;

import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.Bean;

@Bean
public class HelloService {

    public String sayHello() {
        return "Hello World! -- by HelloService";
    }
}
