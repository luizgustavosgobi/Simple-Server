package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.context.ContextHolder;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.InvalidHttpRequestException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.NotFoundHttpException;
import br.com.luizgustavosgobi.simpleServer.http.parser.HttpParser;
import br.com.luizgustavosgobi.simpleServer.http.router.MiddlewareHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.RouteHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;
import br.com.luizgustavosgobi.simpleServer.http.utils.ConnectionUtils;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.nio.channels.SocketChannel;

public class HttpConnectionHandler implements ConnectionHandler {
    private final Router router = ContextHolder.getContext().get(Router.class);

    @Override
    public void onAccept(SocketChannel client) {}

    @Override
    public void onClose(SocketChannel client) {}

    @Override
    public void onRead(SocketChannel client, byte[] data) {
        RequestEntity<?> request;
        ResponseEntity<?> response;
        try { request = HttpParser.parse(data); }
        catch (InvalidHttpRequestException e) {
            ConnectionUtils.sendAndDisconnect(client, e.makeResponse());
            return;
        }

        Logger.Good(client.socket().getInetAddress().getHostAddress() + ": " + request.getMethod() + " " + request.getUri().getPath());

        try {
            RouteHandler routeHandler = router.get(request.getMethod(), request.getUri().getPath());
            if (routeHandler != null) {
                MiddlewareHandler middlewareHandler = router.getMatchingMiddleware(request.getUri().getPath());
                if (middlewareHandler != null) {
                    response = middlewareHandler.handle(request);
                    if (response.isMiddlewarePassed()) {
                        response = routeHandler.handle(request);
                    }
                } else response = routeHandler.handle(request);
            } else throw new NotFoundHttpException("Route " + request.getMethod() + " " + request.getUri().getPath() + " not found");
        } catch (HttpException e) {
            response = e.makeResponse();
        }

        response.getHttpLine().setVersion(response.getHttpLine().getVersion());
        ConnectionUtils.sendAndDisconnect(client, response);
    }
}
