package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.Bean;

@Bean
public class MainService {

    public void greeting() {
        System.out.print("Hi!");
    }
}
