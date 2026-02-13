package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionTable;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.NotFoundHttpException;
import br.com.luizgustavosgobi.simpleServer.http.router.RouteHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

public class HttpConnectionHandler implements ConnectionHandler {
    private final Router router;
    private final ConnectionTable connectionTable;

    public HttpConnectionHandler(Router router, ConnectionTable connectionTable) {
        this.router = router;
        this.connectionTable = connectionTable;
    }

    @Override
    public void onAccept(Client client) {
        client.setAttribute("closeAfterWrite", true);
    }

    @Override
    public void onClose(Client client) {}

    @Override
    public void onWrite(Client client) {}

    @Override
    public void onRead(Client client, Object data) {
        RequestEntity<?> request = (RequestEntity<?>) data;
        ResponseEntity<?> response;

        Logger.Good(client.getAddress() + ": " + request.getMethod() + " " + request.getUri());

        try {
            RouteHandler routeHandler = router.get(request.getMethod(), request.getUri().getPath());
            if (routeHandler != null) {
                //MiddlewareHandler middlewareHandler = router.getMatchingMiddleware(request.getUri().getPath());

//                if (middlewareHandler != null) {
//                    response = middlewareHandler.handle(request);
//
//                    if (response.isMiddlewarePassed()) {
//                        response = routeHandler.handle(request);
//                    }
//                } else response = routeHandler.handle(request);

                response = routeHandler.handle(request);
            } else throw new NotFoundHttpException("Route " + request.getMethod() + " " + request.getUri().getPath() + " not found");
        } catch (HttpException e) {
            response = e.makeResponse();
        }

        response.getHttpLine().setVersion(response.getHttpLine().getVersion());
        client.write(response);
    }
}
