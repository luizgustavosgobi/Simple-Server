package br.com.luizgustavosgobi.simpleServer.http.router;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;

import java.util.Hashtable;
import java.util.regex.Pattern;

public class Router {
    private final Hashtable<HttpMethod, Hashtable<String, RouteHandler>> routes = new Hashtable<>();
    private final Hashtable<String, MiddlewareHandler> middlewares = new Hashtable<>();

    public Router add(HttpMethod method, String path, RouteHandler handler) {
        routes.computeIfAbsent(method, k -> new Hashtable<>());
        routes.get(method).put(path, handler);
        return this;
    }

    public Router registerMiddleware(String pattern, MiddlewareHandler middleware) {
        middlewares.put(pattern, middleware);
        return this;
    }

    public RouteHandler get(HttpMethod method, String path) {
        try { return routes.get(method).get(path); }
        catch (NullPointerException e) { return null; }
    }

    public MiddlewareHandler getMatchingMiddleware(String url) {
        for (String pattern : middlewares.keySet()) {
            if (Pattern.matches(pattern, url)) return middlewares.get(pattern);
        }
        return null;
    }

    public Hashtable<HttpMethod, Hashtable<String, RouteHandler>> getRoutes() {
        return routes;
    }

    public Hashtable<String, MiddlewareHandler> getMiddlewares() {
        return middlewares;
    }
}
