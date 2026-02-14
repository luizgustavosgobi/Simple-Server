package br.com.luizgustavosgobi.simpleServer.http.filters.impl;

import br.com.luizgustavosgobi.simpleServer.core.filters.Filter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterContext;

public class LoggingFilter implements Filter {
    private final String prefix;

    public LoggingFilter(String prefix) {
        this.prefix = prefix;
    }

    public LoggingFilter() {
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

