package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.Bean;

@Bean
public class MainService {

//    private MainService() {
//        System.out.println("ds");
//    }

    public void greeting() {
        System.out.println("Hi from MainService!");
    }
}
