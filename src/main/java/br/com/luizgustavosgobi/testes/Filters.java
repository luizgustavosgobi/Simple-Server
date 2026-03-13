package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.EnableFilters;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterBuilder;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.impl.LoggingFilter;

@EnableFilters
public class Filters {

    public FilterChain chain() {
        return FilterBuilder.create()
                .addFilter(new LoggingFilter())
                .build();
    }
}
