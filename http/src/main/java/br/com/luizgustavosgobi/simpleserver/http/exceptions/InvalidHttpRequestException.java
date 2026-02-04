package br.com.luizgustavosgobi.simpleServer.http.exceptions;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

public class InvalidHttpRequestException extends br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException {

    public InvalidHttpRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public InvalidHttpRequestException() {
        super(HttpStatus.BAD_REQUEST, "Invalid Http Request");
    }
}
