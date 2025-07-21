package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.annotations.Bean;
import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Bean
public @interface Controller {
    String value() default "";

    class Handler implements AnnotationProcessor<Controller> {

        @Override
        public Class<Controller> getAnnotationType() {
            return Controller.class;
        }

        @Override
        public int getPriority() {
            return 3;
        }

        @Override
        public void process(AnnotatedElement element, Annotation __, ApplicationContext applicationContext) throws Exception {
            Class<?> clazz = (Class<?>) element;
            Object controller = applicationContext.get(clazz.getName());

            if (controller == null) {
                throw new IllegalStateException("Controller " + clazz.getName() + " não foi processado como Bean primeiro");
            }
        }
    }
}