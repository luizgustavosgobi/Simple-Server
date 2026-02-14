package br.com.luizgustavosgobi.simpleServer.core.context;

import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

public class BeanDefinition {
    private final String name;
    private final Class<?> type;
    private final BeanScope scope;
    private Object instance;
    private final Constructor<?> constructor;
    private final Class<?>[] constructorDependencies;

    private static final ThreadLocal<Set<String>> resolvingBeans = ThreadLocal.withInitial(HashSet::new);

    public BeanDefinition(String name, Class<?> type, BeanScope scope, Object instance) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.instance = instance;
        this.constructor = null;
        this.constructorDependencies = null;
    }

    public BeanDefinition(String name, Class<?> type, BeanScope scope) {
        this(name, type, scope, null);
    }

    public BeanDefinition(String name, Class<?> type, BeanScope scope, Constructor<?> constructor, Class<?>[] constructorDependencies) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.instance = null;
        this.constructor = constructor;
        this.constructorDependencies = constructorDependencies;
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

    public Object getInstance() {
        return instance;
    }

    public Object getInstance(BeanRegistry beanRegistry) {
        if (instance != null) return instance;

        if (constructor != null && constructorDependencies != null)
            return instantiateBean(beanRegistry);

        return null;
    }

    private Object instantiateBean(BeanRegistry beanRegistry) {
        Set<String> resolving = resolvingBeans.get();
        if (resolving.contains(name))
            throw new IllegalStateException("Circular dependency detected: " + name + " -> " + resolving);

        try {
            resolving.add(name);

            if (constructorDependencies == null || constructor == null) return null;

            Object[] args = new Object[constructorDependencies.length];
            for (int i = 0; i < constructorDependencies.length; i++) {
                Class<?> dependencyType = constructorDependencies[i];
                Object dependency = beanRegistry.getInstance(dependencyType);

                if (dependency == null) {
                    throw new IllegalStateException(
                        "Cannot resolve dependency of type " + dependencyType.getName() +
                        " for bean " + name
                    );
                }

                args[i] = dependency;
            }

            constructor.setAccessible(true);
            instance = constructor.newInstance(args);
            constructor.setAccessible(false);

            Logger.Debug("Bean instantiated: " + type.getSimpleName() + " with " + args.length + " dependencies");

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate bean " + name + ": " + e.getMessage(), e);
        } finally {
            resolving.remove(name);
            if (resolving.isEmpty()) {
                resolvingBeans.remove();
            }
        }
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isInstantiated() {
        return instance != null;
    }

    public boolean isSingleton() {
        return scope == BeanScope.SINGLETON;
    }
}
