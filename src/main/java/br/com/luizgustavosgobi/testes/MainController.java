package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.AutoWired;
import br.com.luizgustavosgobi.simpleServer.http.annotations.Controller;
import br.com.luizgustavosgobi.simpleServer.http.annotations.GetMapping;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

@Controller
public class MainController {

    @AutoWired
    private MainService service;


    @GetMapping(path = "/a")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok("Hello, World!");
    }
}
