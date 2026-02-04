package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class URI {
    private final HashMap<String, String> query = new HashMap<>();
    private final String path;

    public URI(String path) {
        if (path.contains("?")) {
            this.path = path.substring(0, path.indexOf("?"));
            for (String s : path.split("\\?")[1].split("&")) {
                String[] kv = s.split("=");
                this.query.put(kv[0], kv[1]);
            }
        } else this.path = path;
    }

    public String getQueryParam(String key) {
        return this.query.get(key);
    }

    @Override
    public String toString() {
        return this.path;
    }
}
