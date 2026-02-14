package br.com.luizgustavosgobi.simpleServer.core.filters;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;

import java.util.List;

public class FilterChainProxy {
    protected final List<? extends FilterChain> filterChains;

    public FilterChainProxy(List<? extends FilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    public FilterContext doFilter(Client client, Object request) throws Exception {
        FilterContext context = createContext(client, request);

        FilterChain chain = getMatchingChain(context);
        if (chain != null) {
            chain.doFilter(context);
        }

        return context;
    }

    public FilterContext doFilter(Client client, Object request, RequestProcessor processor) throws Exception {
        FilterContext context = createContext(client, request);

        FilterChain chain = getMatchingChain(context);
        if (chain != null) {
            chain.doFilter(context, processor);
        }

        return context;
    }

    protected FilterContext createContext(Client client, Object request) {
        return new FilterContext(client, request);
    }

    protected FilterChain getMatchingChain(FilterContext context) {
        return filterChains.isEmpty() ? null : filterChains.getLast();
    }

    public List<? extends FilterChain> getFilterChains() {
        return filterChains;
    }

    public int getChainCount() {
        return filterChains.size();
    }
}

