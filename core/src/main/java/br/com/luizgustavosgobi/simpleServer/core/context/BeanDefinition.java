package br.com.luizgustavosgobi.simpleServer.core.context;

public class BeanDefinition {
    private final String name;
    private final Class<?> type;
    private final BeanScope scope;
    private final boolean lazyInit;
    private Object instance;

    public BeanDefinition(String name, Class<?> type, BeanScope scope, boolean lazyInit, Object instance) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.lazyInit = lazyInit;
        this.instance = instance;
    }

    public BeanDefinition(String name, Class<?> type, BeanScope scope, Object instance) {
        this(name, type, scope, false, instance);
    }

    public BeanDefinition(String name, Class<?> type, BeanScope scope) {
        this(name, type, scope, true, null);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public BeanScope getScope() {
        return scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public Object getInstance() {
        if (instance == null && lazyInit)
            try {
                instance = type.getConstructor().newInstance();
            } catch (Exception _) {}
        return instance;
    }

    public boolean isSingleton() {
        return scope == BeanScope.SINGLETON;
    }
}
