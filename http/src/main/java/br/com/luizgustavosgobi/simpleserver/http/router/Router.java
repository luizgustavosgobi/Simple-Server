package br.com.luizgustavosgobi.simpleServer.http.router;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;

import java.util.Hashtable;

public class Router {
    private final Hashtable<HttpMethod, Hashtable<String, RouteHandler>> routes = new Hashtable<>();

    public Router add(HttpMethod method, String path, RouteHandler handler) {
        routes.computeIfAbsent(method, k -> new Hashtable<>());
        routes.get(method).put(path, handler);
        return this;
    }

    public RouteHandler get(HttpMethod method, String path) {
        try { return routes.get(method).get(path); }
        catch (NullPointerException e) { return null; }
    }

    public Hashtable<HttpMethod, Hashtable<String, RouteHandler>> getRoutes() {
        return routes;
    }
}
