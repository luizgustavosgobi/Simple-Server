package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpLine;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpResponseLine;

public class ResponseEntity<T> extends HttpEntity<T> {

    public ResponseEntity(HttpLine httpLine, HttpHeaders headers, T body) {
        super(httpLine, headers, body);
    }

    public ResponseEntity(HttpHeaders headers, T body) {
        this(new HttpResponseLine(HttpStatus.OK), headers, body);
    }

    public ResponseEntity(HttpStatus status, T body) {
        this(new HttpResponseLine(status), new HttpHeaders(), body);
    }

    public ResponseEntity() {
        this(new HttpHeaders(), null);
    }

    public static HttpResponseBuilder status(HttpStatus status) {
        return new HttpResponseBuilder(status);
    }

    public static HttpResponseBuilder status(int status) {
        return new HttpResponseBuilder(HttpStatus.valueOf(status));
    }

    public static HttpResponseBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return ok().body(body);
    }

    public static HttpResponseBuilder internalServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getStatus() {
        return ((HttpResponseLine) this.httpLine).getStatus();
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    @Override
    public String toString() {
        return httpLine.toString() + headers.toString() + "\r\n" + body.toString();
    }
}
