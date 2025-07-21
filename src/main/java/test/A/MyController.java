package test.A;

import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.AutoWired;
import br.com.luizgustavosgobi.simpleServer.http.annotations.Controller;
import br.com.luizgustavosgobi.simpleServer.http.annotations.Middleware;
import br.com.luizgustavosgobi.simpleServer.http.annotations.PostMapping;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import test.HiDto;

@Controller
public class MyController {

    @AutoWired private HelloService helloService;

    @AutoWired public Config config;

    @PostMapping(path = "/hello")
    public ResponseEntity<HiDto> hello(HiDto hiDto) {
        return ResponseEntity.status(HttpStatus.OK).body(hiDto);
    }

    @PostMapping(path = "/file")
    public ResponseEntity<?> file() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Middleware(pattern = "/.*")
    public ResponseEntity<?> middleware(RequestEntity<?> request) {
        if (request.getPath().equals("/greeting"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.middlewarePass();
    }
}
