package br.com.luizgustavosgobi.simpleServer.http.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.http.enums.HttpMethod;
import br.com.luizgustavosgobi.simpleServer.http.router.Router;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {
    String path() default "";

    class Handler implements AnnotationDefinition<PostMapping> {

        @Override
        public Class<PostMapping> getAnnotationType() {
            return PostMapping.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.READY;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry applicationContext) throws Exception {
            PostMapping postMapping = (PostMapping) annotation;

            String path = postMapping.path();
            if (path.isEmpty()) { path = "/"; }

            Router router = applicationContext.getInstance(Router.class);
            router.add(HttpMethod.POST, path, RequestMapping.Handler.createHandler((Method) element, applicationContext));
        }
    }
}
