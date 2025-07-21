package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import lombok.Getter;
import lombok.Setter;

public abstract class HttpLine {
    @Getter @Setter
    protected String version;

    public HttpLine(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }
}
