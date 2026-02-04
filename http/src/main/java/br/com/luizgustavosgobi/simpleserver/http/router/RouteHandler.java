package br.com.luizgustavosgobi.simpleServer.http.router;

import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException;

@FunctionalInterface
public interface RouteHandler {
    ResponseEntity<?> handle(RequestEntity<?> request) throws HttpException;
}
