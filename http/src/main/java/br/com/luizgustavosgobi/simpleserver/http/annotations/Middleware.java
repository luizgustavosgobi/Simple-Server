package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.router.MiddlewareHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    String pattern() default "";

    class Handler implements AnnotationDefinition<Middleware> {

        @Override
        public Class<Middleware> getAnnotationType() {
            return Middleware.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.READY;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            Method method = (Method) element;
            Router router = (Router) applicationContext.getBean(Router.class).getInstance();
            Middleware middleware = (Middleware) annotation;
            String pattern = middleware.pattern();

            router.registerMiddleware(pattern != null ? pattern : "/.*",  createMiddlewareHandler(method, applicationContext.getBean(method.getDeclaringClass()).getInstance()));
        }

        private MiddlewareHandler createMiddlewareHandler(Method method, Object instance) {
            return (request) -> {
                try {
                    method.setAccessible(true);
                    return (ResponseEntity<?>) method.invoke(instance, request);
                } catch (Exception e) {
                    Logger.Error(this, "Error invoking middleware method: " + e.getMessage());
                    return ResponseEntity.internalServerError().build();
                }
            };
        }
    }
}
