package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.annotation.annotations.Bean;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Bean
public @interface Controller {
    String value() default "";

    class Handler implements AnnotationDefinition<Controller> {

        @Override
        public Class<Controller> getAnnotationType() {
            return Controller.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.POST_CONSTRUCT;
        }

        @Override
        public void process(AnnotatedElement element, Annotation __, BeanRegistry applicationContext) throws Exception {
            Class<?> clazz = (Class<?>) element;
            Object controller = applicationContext.getBean(clazz.getName());

            if (controller == null) {
                throw new IllegalStateException("Controller " + clazz.getName() + " não foi processado como Bean primeiro");
            }
        }
    }
}