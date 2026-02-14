package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.http.annotations.Controller;
import br.com.luizgustavosgobi.simpleServer.http.annotations.GetMapping;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

@Controller
public class MainController {

    private final MainService service;

    public MainController(MainService service) {
        this.service = service;
    }

    @GetMapping(path = "/a")
    public ResponseEntity<String> greeting() {
        //service.greeting();
        return ResponseEntity.ok("Hello, World! Service called successfully!");
    }
}
