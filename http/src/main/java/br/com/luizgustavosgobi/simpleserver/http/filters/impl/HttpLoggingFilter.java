package br.com.luizgustavosgobi.simpleServer.http.filters.impl;

import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;
import br.com.luizgustavosgobi.simpleServer.core.filters.impl.LoggingFilter;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterContext;

public class HttpLoggingFilter extends LoggingFilter {
    private final String prefix;

    public HttpLoggingFilter(String prefix) {
        this.prefix = prefix;
    }

    public HttpLoggingFilter() {
        this("Filter");
    }

    @Override
    public void doFilter(FilterContext context, FilterChain chain) throws Exception {
        HttpFilterContext httpContext = (HttpFilterContext) context;

        long startTime = System.currentTimeMillis();
        Logger.Info(String.format("[%s] Request: %s %s",
                prefix,
                httpContext.getRequest().getMethod(),
                httpContext.getRequest().getPath()));

        chain.doFilter(context);

        long duration = System.currentTimeMillis() - startTime;
        Logger.Info(String.format("[%s] Completed in %dms", prefix, duration));
    }
}

