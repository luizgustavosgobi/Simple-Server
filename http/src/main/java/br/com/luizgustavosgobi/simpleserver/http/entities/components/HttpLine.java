package br.com.luizgustavosgobi.simpleServer.http.entities.components;

public abstract class HttpLine {
    protected String version;

    public HttpLine(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }
}
