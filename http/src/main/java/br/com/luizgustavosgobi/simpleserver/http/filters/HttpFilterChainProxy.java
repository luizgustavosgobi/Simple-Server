package br.com.luizgustavosgobi.simpleServer.http.filters;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;
import br.com.luizgustavosgobi.simpleServer.core.filters.RequestProcessor;
import br.com.luizgustavosgobi.simpleServer.http.entities.RequestEntity;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;

import java.util.List;

public class HttpFilterChainProxy extends FilterChainProxy {

    public HttpFilterChainProxy(List<HttpFilterChain> filterChains) {
        super(filterChains);
    }

    @Override
    protected HttpFilterContext createContext(Client client, Object request) {
        return new HttpFilterContext(client, (RequestEntity<?>) request);
    }

    @Override
    protected HttpFilterChain getMatchingChain(FilterContext context) {
        HttpFilterContext httpContext = (HttpFilterContext) context;
        String url = httpContext.getRequest().getPath();

        for (FilterChain chain : filterChains) {
            if (chain instanceof HttpFilterChain httpFilterChain) {
                if (httpFilterChain.matches(url)) {
                    return httpFilterChain;
                }
            }
        }

        return null;
    }

    public ResponseEntity<?> doFilter(Client client, RequestEntity<?> request) throws Exception {
        FilterContext context = super.doFilter(client, request);
        return context != null ? (ResponseEntity<?>) context.getResponse() : null;
    }

    public ResponseEntity<?> doFilter(Client client, RequestEntity<?> request, RequestProcessor processor) throws Exception {
        FilterContext context = super.doFilter(client, request, processor);
        return context != null ? (ResponseEntity<?>) context.getResponse() : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HttpFilterChain> getFilterChains() {
        return (List<HttpFilterChain>) filterChains;
    }
}

