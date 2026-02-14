package br.com.luizgustavosgobi.simpleServer.core.filters;

public enum FilterOrder {
    FIRST(-100),
    HIGHEST(-50),
    HIGH(-25),
    NORMAL(0),
    LOW(25),
    LOWEST(50),
    LAST(100);

    private final int order;

    FilterOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }


}

