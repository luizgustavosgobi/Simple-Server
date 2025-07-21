package br.com.luizgustavosgobi.simpleServer.http.exceptions;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;

public class NotFoundHttpException extends HttpException {
    public NotFoundHttpException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
