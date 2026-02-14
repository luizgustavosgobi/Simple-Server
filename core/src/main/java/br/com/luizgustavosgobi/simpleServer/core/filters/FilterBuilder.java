package br.com.luizgustavosgobi.simpleServer.core.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterBuilder<B extends FilterBuilder<B>> {
    protected final List<OrderedFilter> filters = new ArrayList<>();
    protected int defaultOrder = FilterOrder.NORMAL.getOrder();

    @SuppressWarnings("unchecked")
    public B addFilter(Filter filter, int order) {
        filters.add(new OrderedFilter(filter, order));
        return (B) this;
    }

    public B addFilter(Filter filter, FilterOrder filterOrder) {
        return addFilter(filter, filterOrder.getOrder());
    }

    public B addFilter(Filter filter) {
        return addFilter(filter, defaultOrder++);
    }

    public B addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        int referenceOrder = findFilterOrder(beforeFilter);
        return addFilter(filter, referenceOrder - 1);
    }

    public B addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        int referenceOrder = findFilterOrder(afterFilter);
        return addFilter(filter, referenceOrder + 1);
    }

    @SuppressWarnings("unchecked")
    public B defaultOrder(int order) {
        this.defaultOrder = order;
        return (B) this;
    }

    public FilterChain build() {
        Collections.sort(filters);

        List<Filter> orderedFilters = filters.stream()
                .map(OrderedFilter::getFilter)
                .toList();

        return new FilterChain(orderedFilters);
    }

    protected int findFilterOrder(Class<? extends Filter> filterClass) {
        for (OrderedFilter orderedFilter : filters) {
            if (filterClass.isInstance(orderedFilter.getFilter())) {
                return orderedFilter.getOrder();
            }
        }
        return FilterOrder.NORMAL.getOrder();
    }

    public static FilterBuilder<?> create() {
        return new FilterBuilder<>();
    }
}

