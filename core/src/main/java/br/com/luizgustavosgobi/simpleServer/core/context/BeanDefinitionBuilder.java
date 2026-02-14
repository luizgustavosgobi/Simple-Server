package br.com.luizgustavosgobi.simpleServer.core.context;

public class BeanDefinitionBuilder {
    private String name;
    private Class<?> type;
    private BeanScope scope = BeanScope.SINGLETON;
    private Object instance = null;

    public static BeanDefinitionBuilder builder() {
        return new BeanDefinitionBuilder();
    }

    public BeanDefinitionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BeanDefinitionBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public BeanDefinitionBuilder scope(BeanScope scope) {
        this.scope = scope;
        return this;
    }

    public BeanDefinitionBuilder instance(Object instance) {
        this.instance = instance;
        return this;
    }

    public BeanDefinitionBuilder singleton() {
        this.scope = BeanScope.SINGLETON;
        return this;
    }

    public BeanDefinitionBuilder prototype() {
        this.scope = BeanScope.PROTOTYPE;
        return this;
    }

    public BeanDefinitionBuilder request() {
        this.scope = BeanScope.REQUEST;
        return this;
    }

    public BeanDefinitionBuilder session() {
        this.scope = BeanScope.SESSION;
        return this;
    }

    public BeanDefinition build() {
        if (name == null || type == null) {
            throw new IllegalStateException("Bean name and type is required!");
        }

        return new BeanDefinition(name, type, scope, instance);
    }
}
