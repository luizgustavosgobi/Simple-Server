package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
import br.com.luizgustavosgobi.simpleServer.core.filters.FilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterChainProxy;
import br.com.luizgustavosgobi.simpleServer.http.filters.HttpFilterChain;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableWebFilters {

    class Handler implements AnnotationDefinition<EnableWebFilters> {

        @Override
        public Class<EnableWebFilters> getAnnotationType() {
            return EnableWebFilters.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.CONFIGURATION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            Class<?> configClass = (Class<?>) element;

            Object configInstance = configClass.getConstructor().newInstance();

            //Object configInstance = applicationContext.getInstance(configClass);

            List<HttpFilterChain> chains = new ArrayList<>();
            for (Method method : configClass.getDeclaredMethods()) {
                if (HttpFilterChain.class.isAssignableFrom(method.getReturnType())) {
                    method.setAccessible(true);

                    try {
                        HttpFilterChain chain = (HttpFilterChain) method.invoke(configInstance);
                        if (chain != null) {
                            chains.add(chain);
                        }
                    } catch (Exception e) {
                        Logger.Error(this, "Failed to invoke filter chain method: " + method.getName() + " - " + e.getMessage());
                    }
                }
            }

            if (!chains.isEmpty()) {
                FilterChainProxy filterChainProxy = new HttpFilterChainProxy(chains);
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


