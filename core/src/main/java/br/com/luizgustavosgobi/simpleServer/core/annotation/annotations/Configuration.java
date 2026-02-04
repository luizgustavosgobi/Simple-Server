package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Configuration {

    class ConfigurationHandler implements AnnotationDefinition<Configuration> {

        @Override
        public Class<Configuration> getAnnotationType() {
            return Configuration.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.CONFIGURATION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {}
    }
}
