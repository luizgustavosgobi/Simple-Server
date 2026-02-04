package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.AutoWired;
import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.Bean;

@Bean
public class MainController {

    @AutoWired
    private MainService service;



}
