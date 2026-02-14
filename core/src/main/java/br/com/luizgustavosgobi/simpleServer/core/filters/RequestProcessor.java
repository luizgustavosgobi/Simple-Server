package br.com.luizgustavosgobi.simpleServer.core.filters;

@FunctionalInterface
public interface RequestProcessor {
    void process(FilterContext context) throws Exception;
}

