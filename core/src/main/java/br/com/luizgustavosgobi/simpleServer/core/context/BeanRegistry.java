package br.com.luizgustavosgobi.simpleServer.core.context;

import java.util.List;

public interface BeanRegistry {
    void register(BeanDefinition bean);
    void register(BeanDefinition... beans);

    BeanDefinition getBean(String name);
    BeanDefinition getBean(Class<?> type);

    <T> T getInstance(Class<?> type);

    boolean containsBean(String name);
    boolean containsBean(Class<?> type);

    List<BeanDefinition> getAllBeans();
}
