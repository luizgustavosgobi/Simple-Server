package br.com.luizgustavosgobi.simpleServer.http.entities;

import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpHeaders;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpLine;
import br.com.luizgustavosgobi.simpleServer.http.parser.components.HttpResponseLine;
import lombok.Getter;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ResponseEntity<T> extends HttpEntity<T> {
    @Getter private boolean isMiddlewarePassed = false;

    // -----------------------------------
    //      Constructor
    // -----------------------------------

    public ResponseEntity(HttpLine httpLine, HttpHeaders headers, T body) {
        super(httpLine, headers, body);
    }

    public ResponseEntity(HttpHeaders headers, T body) {
        this(new HttpResponseLine(HttpStatus.OK), headers, body);
    }

    public ResponseEntity(HttpStatus status, T body) {
        this(new HttpResponseLine(status), new HttpHeaders(), body);
    }

    public ResponseEntity(boolean middlewarePassed) {
        this(new HttpHeaders(), null);
        this.isMiddlewarePassed = middlewarePassed;
    }

    //-------------------
    //      Methods
    //-------------------

    public HttpStatus getStatusCode() {
        return ((HttpResponseLine) this.httpLine).getStatus();
    }
    public void passMiddleware() {
        this.isMiddlewarePassed = true;
    }

    //--------------------------
    //      Static Methods
    //--------------------------

    public static GenericBuilder status(HttpStatus status) {
        return new DefaultBuilder(status);
    }
    public static GenericBuilder status(int status) {
        return new DefaultBuilder(status);
    }

    public static ResponseEntity<?> middlewarePass() {
        return new ResponseEntity<>(true);
    }

    //--------------------------
    //      Auxiliary Methods
    //--------------------------

    public static GenericBuilder ok() {
        return status(HttpStatus.OK);
    }
    public static <T> ResponseEntity<T> ok(T body) {
        return ok().body(body);
    }
    public static GenericBuilder internalServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //--------------------------
    //      Data Methods
    //--------------------------

    @Override
    public String toString() {
        return httpLine.toString() + headers.toString() + "\r\n" + body.toString();
    }

    public byte[] getBytes() {
        return this.toString().getBytes();
    }

    //-------------------------------------
    //           Builder
    //-------------------------------------

    private static class DefaultBuilder implements GenericBuilder {
        private final HttpStatus status;
        private final HttpHeaders headers;

        public DefaultBuilder(HttpStatus status) {
            this.headers = new HttpHeaders();
            this.status = status;
        }

        public DefaultBuilder(int status) {
            this(HttpStatus.valueOf(status));
        }

        // Header Implementation

        public DefaultBuilder header(String headerName, String... headerValues) {
            for(String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }

            return this;
        }

        public DefaultBuilder headers(HttpHeaders headers) {
            if (headers != null) {
                this.headers.addAll(headers);
            }

            return this;
        }


        public DefaultBuilder allow(HttpMethod... allowedMethods) {
            StringBuilder methods = new StringBuilder();
            for (HttpMethod allowedMethod : allowedMethods) {
                methods.append(allowedMethod.name()).append(",");
            }

            this.headers.add("Allow", methods.toString());
            return this;
        }

        public DefaultBuilder location(URI location) {
            this.headers.add("Location", location.toASCIIString());
            return this;
        }

        public <T> ResponseEntity<T> build() {
            return this.body(null);
        }

        // Body Implementations

        public DefaultBuilder contentType(String contentType) {
            this.headers.add("Content-Type", contentType);
            return this;
        }

        public DefaultBuilder contentLength(long contentLength) {
            this.headers.add("Content-Length", String.valueOf(contentLength));
            return this;
        }

        public <T> ResponseEntity<T> body(T body) {
            contentType(body.getClass().getName());
            contentLength(body.toString().getBytes(StandardCharsets.UTF_8).length);
            return new ResponseEntity<>(new HttpResponseLine(status), this.headers, body);
        }
    }

    public interface GenericBuilder extends HeaderBuilder<GenericBuilder>, BodyBuilder<GenericBuilder> {}
    
    private interface BodyBuilder<C extends BodyBuilder<C>> {
        C contentType(String contentType);
        C contentLength(long contentLength);

        <T> ResponseEntity<T> body(T body);
    }

    private interface HeaderBuilder<C extends HeaderBuilder<C>> {
        C header(String headerName, String... headerValues);
        C headers(HttpHeaders headers);

        C allow(HttpMethod... allowedMethods);

        C location(URI location);

        <T> ResponseEntity<T> build();
    }
}
