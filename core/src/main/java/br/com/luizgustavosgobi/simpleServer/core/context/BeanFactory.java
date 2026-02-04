package br.com.luizgustavosgobi.simpleServer.core.context;

public interface BeanFactory {
    <T> T createBean(Class<T> type);
    boolean canCreate(Class<?> type);
}
