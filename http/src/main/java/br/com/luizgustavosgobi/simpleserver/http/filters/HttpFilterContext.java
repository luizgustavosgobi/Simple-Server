package br.com.luizgustavosgobi.simpleServer.http.filters;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

public class HttpFilterContext extends FilterContext {

    public HttpFilterContext(Client client, RequestEntity<?> request) {
        super(client, request);
    }

    public RequestEntity<?> getRequest() {
        return (RequestEntity<?>) request;
    }

    public ResponseEntity<?> getResponse() {
        return (ResponseEntity<?>) response;
    }

    public void setResponse(ResponseEntity<?> response) {
        this.response = response;
    }
}

