package br.com.luizgustavosgobi.simpleServer.http.router;

import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

@FunctionalInterface
public interface MiddlewareHandler {
    ResponseEntity<?> handle(RequestEntity<?> request);
}
