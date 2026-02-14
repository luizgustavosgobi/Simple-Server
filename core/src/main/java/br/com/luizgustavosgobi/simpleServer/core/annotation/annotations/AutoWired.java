package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

@Target({ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoWired {

    class AutoWiredHandler implements AnnotationDefinition<AutoWired> {

        @Override
        public Class<AutoWired> getAnnotationType() {
            return AutoWired.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.DEPENDENCY_INJECTION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            if (element instanceof  Field field) {
                Object declaringClass = applicationContext.getInstance(field.getDeclaringClass());
                Object bean = applicationContext.getInstance(field.getType());

                field.setAccessible(true);
                field.set(declaringClass, bean);
                field.setAccessible(false);

                Logger.Debug("AutoWired " + declaringClass.getClass().getSimpleName() + " " + field.getName());
            }
        }
    }
}
