package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import lombok.Getter;

@Getter
public class HttpResponseLine extends HttpLine {
    private final HttpStatus status;

    public HttpResponseLine(String version, HttpStatus status) {
        super(version);
        this.status = status;
    }

    public HttpResponseLine(HttpStatus status) {
        this("HTTP/1.1", status);
    }

    @Override
    public String toString() {
        return version + " " + status + "\r\n";
    }
}
