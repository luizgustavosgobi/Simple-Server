package br.com.luizgustavosgobi.simpleServer.core.filters;

@FunctionalInterface
public interface Filter {
    void doFilter(FilterContext context, FilterChain chain) throws Exception;
}

