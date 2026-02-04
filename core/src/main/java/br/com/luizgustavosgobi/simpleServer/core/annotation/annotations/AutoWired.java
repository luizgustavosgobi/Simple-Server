package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
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
            Field field = (Field) element;

            BeanDefinition declaringBean = applicationContext.getBean(field.getDeclaringClass());
            BeanDefinition bean = applicationContext.getBean(field.getType());

            field.setAccessible(true);
            field.set(declaringBean.getInstance(), bean.getInstance());
            field.setAccessible(false);

            Logger.Debug("AutoWired " + declaringBean.getName() + " " + field.getName() + " with " + bean.getName());
        }
    }
}
