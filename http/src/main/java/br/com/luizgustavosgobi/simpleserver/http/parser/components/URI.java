package br.com.luizgustavosgobi.simpleServer.http.parser.components;

import java.util.HashMap;

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

    public HashMap<String, String> getQueryParameters() {
        return query;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return this.path;
    }
}
