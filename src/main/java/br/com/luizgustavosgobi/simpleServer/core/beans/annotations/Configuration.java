package br.com.luizgustavosgobi.simpleServer.core.beans.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Configuration {

    class Handler implements AnnotationProcessor<Configuration> {

        @Override
        public Class<Configuration> getAnnotationType() {
            return Configuration.class;
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {}
    }
}
