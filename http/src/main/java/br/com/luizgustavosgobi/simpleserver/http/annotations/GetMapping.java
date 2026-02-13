package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.router.RouteHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String path() default "";
    String value() default "";

    class Handler implements AnnotationDefinition<GetMapping> {

        @Override
        public Class<GetMapping> getAnnotationType() {
            return GetMapping.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.READY;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            GetMapping getMapping = (GetMapping) annotation;
            Method method = (Method) element;

            String path = getMapping.path();
            if (path.isEmpty()) { path = "/"; }

            Router router = applicationContext.getInstance(Router.class);

            RouteHandler handler = RequestMapping.Handler.createHandler(method, applicationContext);
            router.add(HttpMethod.GET, path, handler);
        }
    }
}
