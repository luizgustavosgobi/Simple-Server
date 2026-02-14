package br.com.luizgustavosgobi.simpleServer.core.filters.impl;

import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.core.filters.Filter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterContext;

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
        long startTime = System.currentTimeMillis();

        Logger.Info(String.format("[%s] Request: %s", prefix, context.getClient().getAddress()));

        chain.doFilter(context);

        long duration = System.currentTimeMillis() - startTime;
        Logger.Info(String.format("[%s] Completed in %dms", prefix, duration));
    }
}

