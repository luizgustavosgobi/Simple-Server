package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String path() default "";

    class Handler implements AnnotationProcessor<GetMapping> {

        @Override
        public Class<GetMapping> getAnnotationType() {
            return GetMapping.class;
        }

        @Override
        public int getPriority() {
            return 4;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {
            GetMapping getMapping = element.getAnnotation(GetMapping.class);

            String path = getMapping.path();
            if (path.isEmpty()) { path = "/"; }

            Object instance = applicationContext.get(((Method) element).getDeclaringClass());
            Router router = (Router) applicationContext.get(Router.class);
            router.add(HttpMethod.GET, path, RequestMapping.Handler.createHandler((Method) element, applicationContext));
        }
    }
}
