package br.com.luizgustavosgobi.simpleServer.core.beans.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String name() default "";

    class Handler implements AnnotationProcessor<Bean> {

        @Override
        public Class<Bean> getAnnotationType() {
            return Bean.class;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {
            if (element instanceof Class<?>) {
                Class<?> clazz = (Class<?>) element;
                Object instance = clazz.getDeclaredConstructor().newInstance();
                applicationContext.register(instance);
            } else if (element instanceof Method) {
                Method method = (Method) element;

                List<Object> parameters = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    parameters.add(applicationContext.get(parameter.getType()));
                }

                Object bean =  null;
                Object instance = applicationContext.get(((Method) element).getDeclaringClass());
                if (!parameters.isEmpty())
                    bean = method.invoke(instance, parameters);
                else
                    bean = method.invoke(instance);

                applicationContext.register(bean);
            }
        }
    }
}
