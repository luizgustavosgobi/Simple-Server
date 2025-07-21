package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.core.context.ApplicationContext;
import br.com.luizgustavosgobi.simpleServer.http.entities.ResponseEntity;
import br.com.luizgustavosgobi.simpleServer.http.router.MiddlewareHandler;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    String pattern() default "";

    class Handler implements AnnotationProcessor<Middleware> {

        @Override
        public Class<Middleware> getAnnotationType() {
            return Middleware.class;
        }

        @Override
        public int getPriority() {
            return 4;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, ApplicationContext applicationContext) throws Exception {
            Method method = (Method) element;
            Router router = applicationContext.get(Router.class);
            Middleware middleware = (Middleware) annotation;
            String pattern = middleware.pattern();

            router.registerMiddleware(pattern != null ? pattern : "/.*",  createMiddlewareHandler(method, applicationContext.get(method.getDeclaringClass())));
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
