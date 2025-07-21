package br.com.luizgustavosgobi.simpleServer.core.beans.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

@Target({ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWired {

    class Handler implements AnnotationProcessor<AutoWired> {

        @Override
        public Class<AutoWired> getAnnotationType() {
            return AutoWired.class;
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {
            Field field = (Field) element;
            Object declaringClass = applicationContext.get(field.getDeclaringClass());
            Object value = applicationContext.get(field.getType());
            field.setAccessible(true);
            field.set(declaringClass, value);
            field.setAccessible(false);
        }
    }
}
