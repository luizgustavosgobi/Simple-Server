package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;

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

    class BeanHandler implements AnnotationDefinition<Bean> {

        @Override
        public Class<Bean> getAnnotationType() {
            return Bean.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.BEAN_CREATION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            switch (element.getClass().getSimpleName()) {
                case "Class" -> {
                    Class<?> clazz = (Class<?>) element;

                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    BeanDefinition bean = new BeanDefinition(clazz.getName(), clazz, BeanScope.SINGLETON, instance);
                    applicationContext.register(bean);
                }

                case "Method" -> {
                    Method method = (Method) element;

                    List<Object> parameters = new ArrayList<>();

                    for (Parameter parameter : method.getParameters())
                        parameters.add(applicationContext.getBean(parameter.getType()).getInstance());

                    Object classInstance = applicationContext.getBean(method.getDeclaringClass());
                    Object beanInstance =  method.invoke(classInstance, parameters.isEmpty() ? null : parameters.toArray());

                    BeanDefinition bean = new BeanDefinition(beanInstance.getClass().getName(), beanInstance.getClass(), BeanScope.SINGLETON, beanInstance);
                    applicationContext.register(bean);
                }

                default -> throw new IllegalStateException("Unexpected value: " + element.getClass().getSimpleName());
            }
        }
    }
}
