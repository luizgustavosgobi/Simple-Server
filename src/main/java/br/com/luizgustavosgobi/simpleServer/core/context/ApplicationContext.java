package br.com.luizgustavosgobi.simpleServer.core.context;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, Object> resources = new HashMap<>();

    public ApplicationContext register(String name, Object bean) {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length == 1) {
            resources.put(interfaces[0].getName(), bean);
            return this;
        }

        resources.put(name, bean);
        return this;
    }

    public ApplicationContext register(Class<?> type, Object bean) {
        return register(type.getName(), bean);
    }

    public ApplicationContext register(Object bean) {
        return this.register(bean.getClass().getName(), bean);
    }

    public ApplicationContext register(Object ...bean) {
        for (Object object : bean) {
            register(object.getClass().getName(), object);
        }

        return this;
    }

    public Object get(String name) {
        return resources.get(name);
    }

    public <T> T get(Class<T> type) {
        Class<?>[] interfaces = type.getInterfaces();

        String className = type.getName();
        if (interfaces.length == 1) className = interfaces[0].getName();

        Object bean = resources.get(className);
        if (type.isInstance(bean)) return type.cast(bean);

        return null;
    }

    public Map<String, Object> getAll() {
        return new HashMap<>(resources);
    }
}