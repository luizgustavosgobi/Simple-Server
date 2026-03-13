package br.com.luizgustavosgobi.simpleServer.core.annotation.annotations;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;
import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationPriority;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanDefinition;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanRegistry;
import br.com.luizgustavosgobi.simpleServer.core.context.BeanScope;
import br.com.luizgustavosgobi.simpleServer.core.converter.ConverterPipeline;
import br.com.luizgustavosgobi.simpleServer.core.converter.ConverterPipelineProxy;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDataConverters {

    class EnableDataConvertersHandler implements AnnotationDefinition<EnableDataConverters> {

        @Override
        public Class<EnableDataConverters> getAnnotationType() {
            return EnableDataConverters.class;
        }

        @Override
        public AnnotationPriority getPriority() {
            return AnnotationPriority.CONFIGURATION;
        }

        @Override
        public void process(AnnotatedElement element, Annotation annotation, BeanRegistry registry) throws Exception {
            Class<?> configClass = (Class<?>) element;
            Object configInstance = configClass.getConstructor().newInstance();

            List<ConverterPipeline> pipelines = new ArrayList<>();
            for (var method : configClass.getDeclaredMethods()) {
                if (ConverterPipeline.class.isAssignableFrom(method.getReturnType())) {
                    try {
                        method.setAccessible(true);
                        ConverterPipeline pipeline = (ConverterPipeline) method.invoke(configInstance);
                        method.setAccessible(false);

                        if (pipeline != null) pipelines.add(pipeline);
                    } catch (Exception e) {
                        Logger.Error(this, "Failed to invoke data converter method: " + method.getName() + " - " + e.getMessage());
                    }
                }
            }

            if (!pipelines.isEmpty()) {
                ConverterPipelineProxy proxy = new ConverterPipelineProxy(pipelines);
                registry.register(
                        new BeanDefinition(
                                "CONVERTER_PIPELINE_PROXY",
                                ConverterPipelineProxy.class,
                                BeanScope.SINGLETON,
                                proxy
                        )
                );
            }
        }
    }
}
