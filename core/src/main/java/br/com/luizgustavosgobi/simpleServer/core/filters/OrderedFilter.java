package br.com.luizgustavosgobi.simpleServer.core.filters;

public class OrderedFilter implements Comparable<OrderedFilter> {
    private final Filter filter;
    private final int order;

    public OrderedFilter(Filter filter, int order) {
        this.filter = filter;
        this.order = order;
    }

    public OrderedFilter(Filter filter, FilterOrder filterOrder) {
        this(filter, filterOrder.getOrder());
    }

    public Filter getFilter() {
        return filter;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(OrderedFilter other) {
        return Integer.compare(this.order, other.order);
    }
}

