package br.com.luizgustavosgobi.simpleServer.http.exceptions;

import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

public abstract class HttpException extends RuntimeException {
    private final HttpStatus status;

    public HttpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getCode() { return status; }

    @Override
    public String toString() {
        return status + " " + super.toString();
    }

    public String json() {
        return "{\"status\":\"" + status + "\",\"message\":\"" + getMessage() + "\"}";
    }

    public ResponseEntity<String> makeResponse() {
        return new ResponseEntity<>(status, json());
    }
}
