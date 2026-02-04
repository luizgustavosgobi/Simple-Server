package br.com.luizgustavosgobi.simpleServer.http.exceptions;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

public class InternalServerErrorException extends br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException {

    public InternalServerErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerErrorException() { super(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"); }
}
