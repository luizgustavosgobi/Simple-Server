package br.com.luizgustavosgobi.simpleServer.http.filters.impl;

import br.com.luizgustavosgobi.simpleServer.core.filters.Filter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpStatus;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterContext;

public class CorsFilter implements Filter {
    private final String allowedOrigins;
    private final String allowedMethods;
    private final String allowedHeaders;

    public CorsFilter(String allowedOrigins, String allowedMethods, String allowedHeaders) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
    }

    public CorsFilter() {
        this("*", "GET,POST,PUT,DELETE,OPTIONS", "*");
    }

    @Override
    public void doFilter(FilterContext context, FilterChain chain) throws Exception {
        HttpFilterContext httpContext = (HttpFilterContext) context;

        if ("OPTIONS".equalsIgnoreCase(httpContext.getRequest().getMethod().name())) {
            ResponseEntity<?> response = (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
                    .header("Access-Control-Allow-Origin", allowedOrigins)
                    .header("Access-Control-Allow-Methods", allowedMethods)
                    .header("Access-Control-Allow-Headers", allowedHeaders)
                    .build();

            context.setResponse(response);
            context.stopChain();
            return;
        }

        context.setAttribute("cors.allowedOrigins", allowedOrigins);
        chain.doFilter(context);

        if (httpContext.getResponse() != null) {
            httpContext.getResponse().getHeaders().add("Access-Control-Allow-Origin", allowedOrigins);
            httpContext.getResponse().getHeaders().add("Access-Control-Allow-Methods", allowedMethods);
            httpContext.getResponse().getHeaders().add("Access-Control-Allow-Headers", allowedHeaders);
        }
    }
}

