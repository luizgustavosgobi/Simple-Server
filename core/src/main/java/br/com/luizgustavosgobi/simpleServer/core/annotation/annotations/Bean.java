package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
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

                    Constructor<?>[] constructors = clazz.getConstructors();
                    if (constructors.length == 0)
                        throw new IllegalStateException("Cannot instantiate bean " + clazz.getSimpleName() + ": no public constructors");

                    Constructor<?> bestConstructor = null;
                    int maxParams = -1;
                    Class<?>[] bestDependencies = null;

                    for (Constructor<?> constructor : constructors) {
                        Parameter[] parameters = constructor.getParameters();
                        List<Class<?>> dependencies = new ArrayList<>();

                        for (Parameter parameter : parameters) {
                            Class<?> paramType = parameter.getType();
                            dependencies.add(paramType);
                        }

                        if (parameters.length > maxParams) {
                            bestConstructor = constructor;
                            maxParams = parameters.length;
                            bestDependencies = dependencies.toArray(new Class<?>[0]);
                        }
                    }


                    BeanDefinition bean = new BeanDefinition(
                            clazz.getName(),
                            clazz,
                            BeanScope.SINGLETON,
                            bestConstructor,
                            bestDependencies
                    );

                    applicationContext.register(bean);
                    Logger.Debug("Bean registered: " + clazz.getSimpleName() + " with constructor having " + maxParams + " dependencies");
                }

                case "Method" -> {
                    Method method = (Method) element;

                    List<Object> parameters = new ArrayList<>();
                    boolean allResolved = true;

                    for (Parameter parameter : method.getParameters()) {
                        Object paramInstance = applicationContext.getInstance(parameter.getType());
                        if (paramInstance == null) {
                            allResolved = false;
                            break;
                        }
                        parameters.add(paramInstance);
                    }

                    if (!allResolved) return;

                    Object classInstance = applicationContext.getInstance(method.getDeclaringClass());
                    if (classInstance == null) return;

                    Object beanInstance = method.invoke(classInstance, parameters.isEmpty() ? null : parameters.toArray());

                    BeanDefinition bean = new BeanDefinition(
                            beanInstance.getClass().getName(),
                            beanInstance.getClass(),
                            BeanScope.SINGLETON,
                            beanInstance
                    );

                    applicationContext.register(bean);
                }

                default -> throw new IllegalStateException("Unexpected value: " + element.getClass().getSimpleName());
            }
        }
    }
}
