package test.A;

import br.com.luizgustavosgobi.simpleServer.http.annotations.Controller;
import br.com.luizgustavosgobi.simpleServer.http.annotations.GetMapping;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

@Controller
public class SecondController {

    @GetMapping(path = "/poo")
    public ResponseEntity<String> poo() {
        return ResponseEntity.status(HttpStatus.OK).body("Poo");
    }
}
