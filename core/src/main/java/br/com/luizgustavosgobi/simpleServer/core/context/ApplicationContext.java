package br.com.luizgustavosgobi.simpleServer.core.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements BeanRegistry {
    private final Map<String, BeanDefinition> beans = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> typesToNameMapping = new ConcurrentHashMap<>();

    @Override
    public void register(BeanDefinition bean) {
        if (bean == null)
            throw new IllegalArgumentException("Bean cannot be null");

        beans.put(bean.getName(), bean);
        typesToNameMapping.put(bean.getType(), bean.getName());

        for (Class<?> iface : bean.getType().getInterfaces()) {
            typesToNameMapping.put(iface, bean.getName());
        }
    }

    @Override
    public void register(BeanDefinition ...beans) {
        for (BeanDefinition bean : beans) {
            register(bean);
        }
    }

    @Override
    public BeanDefinition getBean(String name) {
        if (name == null)
            throw new IllegalArgumentException("No bean found with the name: "+ name);
        return beans.get(name);
    }

    @Override
    public BeanDefinition getBean(Class<?> type) {
        String beanName = typesToNameMapping.get(type);
        if (beanName == null)
            throw new IllegalArgumentException("No bean found with the type: "+ type.getName());

        return beans.get(beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return beans.containsKey(name);
    }

    @Override
    public boolean containsBean(Class<?> type) {
        return typesToNameMapping.containsKey(type);
    }

    @Override
    public List<BeanDefinition> getAllBeans() {
        Collection<BeanDefinition> coll = beans.values();

        if (coll instanceof List)
            return (List<BeanDefinition>)coll;
        else
            return new ArrayList<>(coll);
    }
}