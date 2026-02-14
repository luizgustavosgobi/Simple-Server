package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChain;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableFilters {

    class Handler implements AnnotationDefinition<EnableFilters> {

        @Override
        public Class<EnableFilters> getAnnotationType() {
            return EnableFilters.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.CONFIGURATION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            Class<?> configClass = (Class<?>) element;
            Object configInstance = applicationContext.getInstance(configClass);

            List<FilterChain> chains = new ArrayList<>();
            for (Method method : configClass.getDeclaredMethods()) {
                if (FilterChain.class.isAssignableFrom(method.getReturnType())) {
                    method.setAccessible(true);

                    try {
                        FilterChain chain = (FilterChain) method.invoke(configInstance);
                        if (chain != null) {
                            chains.add(chain);
                        }
                    } catch (Exception e) {
                        Logger.Error(this, "Failed to invoke filter chain method: " + method.getName() + " - " + e.getMessage());
                    }
                }
            }

            if (!chains.isEmpty()) {
                FilterChainProxy filterChainProxy = new FilterChainProxy(chains);
                applicationContext.register(
                    new BeanDefinition(
                        "FILTER_CHAIN_PROXY",
                        FilterChainProxy.class,
                        BeanScope.SINGLETON,
                        filterChainProxy
                    )
                );
            }
        }
    }
}


