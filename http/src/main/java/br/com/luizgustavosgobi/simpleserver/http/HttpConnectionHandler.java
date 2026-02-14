package br.com.luizgustavosgobi.simpleServer.http;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.connection.ConnectionHandler;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.HttpException;
import br.com.luizgustavosgobi.simpleServer.http.exceptions.NotFoundHttpException;
import br.com.luizgustavosgobi.simpleServer.http.router.RouteHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

public class HttpConnectionHandler implements ConnectionHandler {
    private final Router router;

    public HttpConnectionHandler(Router router) {
        this.router = router;
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

        try {
            RouteHandler routeHandler = router.get(request.getMethod(), request.getUri().getPath());

            if (routeHandler != null) response = routeHandler.handle(request);
            else throw new NotFoundHttpException("Route " + request.getMethod() + " " + request.getUri().getPath() + " not found");
        } catch (HttpException e) {
            response = e.makeResponse();
        } catch (Exception e) {
            Logger.Error(this, "Error processing request: " + e.getMessage());
            response = ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
        }

        if (response != null) {
            response.getHttpLine().setVersion(request.getVersion());
            client.write(response);
        }
    }
}
