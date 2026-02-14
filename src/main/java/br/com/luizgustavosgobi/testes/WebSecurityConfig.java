package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.filters.FilterOrder;
import br.com.luizgustavosgobi.simpleServer.http.annotations.EnableWebFilters;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterBuilder;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterChain;
import br.com.luizgustavosgobi.simpleServer.http.filters.impl.CorsFilter;
import br.com.luizgustavosgobi.simpleServer.http.filters.impl.LoggingFilter;

@EnableWebFilters
public class WebSecurityConfig {

    public HttpFilterChain apiFilterChain() {
        return HttpFilterBuilder.create()
                .forPattern("/api/.*")
                .addFilter(new LoggingFilter("API"), FilterOrder.FIRST)
                .addFilter(new CorsFilter(), FilterOrder.HIGH)
                .build();
    }

    public HttpFilterChain publicFilterChain() {
        return HttpFilterBuilder.create()
                .forPattern("/public/.*")
                .addFilter(new LoggingFilter("PUBLIC"), FilterOrder.FIRST)
                .build();
    }

    public HttpFilterChain defaultFilterChain() {
        return HttpFilterBuilder.create()
                .forPattern(".*")
                .addFilter(new LoggingFilter("DEFAULT"), FilterOrder.NORMAL)
                .build();
    }
}

