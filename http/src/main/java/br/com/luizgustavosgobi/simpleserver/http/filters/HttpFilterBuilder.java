package br.com.luizgustavosgobi.simpleServer.http.filters;

import br.com.luizgustavosgobi.simpleServer.core.filters.Filter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterBuilder;
import br.com.luizgustavosgobi.simpleServer.core.filters.OrderedFilter;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterOrder;

import java.util.Collections;
import java.util.List;

public class HttpFilterBuilder extends FilterBuilder<HttpFilterBuilder> {
    protected String urlPattern = ".*";


    public HttpFilterBuilder forPattern(String pattern) {
        this.urlPattern = pattern;
        return this;
    }

    @Override
    public HttpFilterBuilder addFilter(Filter filter, FilterOrder filterOrder) {
        super.addFilter(filter, filterOrder);
        return this;
    }

    @Override
    public HttpFilterChain build() {
        Collections.sort(filters);
        List<Filter> orderedFilters = filters.stream()
                .map(OrderedFilter::getFilter)
                .toList();

        return new HttpFilterChain(urlPattern, orderedFilters);
    }

    public static HttpFilterBuilder create() {
        return new HttpFilterBuilder();
    }
}
