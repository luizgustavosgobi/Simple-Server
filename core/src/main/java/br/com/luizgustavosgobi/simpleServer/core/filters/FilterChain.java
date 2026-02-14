package br.com.luizgustavosgobi.simpleServer.core.filters;

import java.util.List;


public class FilterChain {
    protected final List<Filter> filters;
    private final ThreadLocal<Integer> currentPosition = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<RequestProcessor> requestProcessor = new ThreadLocal<>();

    public FilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    public void doFilter(FilterContext context) throws Exception {
        if (context.isChainStopped()) {
            currentPosition.remove();
            requestProcessor.remove();
            return;
        }

        int position = currentPosition.get();
        try {
            if (position < filters.size()) {
                Filter filter = filters.get(position);

                currentPosition.set(position + 1);
                filter.doFilter(context, this);
            } else {
                RequestProcessor processor = requestProcessor.get();
                if (processor != null) {
                    processor.process(context);
                }
            }
        } finally {
            if (position == 0) {
                currentPosition.remove();
                requestProcessor.remove();
            }
        }
    }

    public void doFilter(FilterContext context, RequestProcessor processor) throws Exception {
        try {
            requestProcessor.set(processor);
            doFilter(context);
        } finally {
            requestProcessor.remove();
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public int size() {
        return filters.size();
    }
}

