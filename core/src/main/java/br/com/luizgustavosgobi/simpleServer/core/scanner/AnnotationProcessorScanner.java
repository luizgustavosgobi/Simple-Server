package br.com.luizgustavosgobi.simpleServer.core.scanner;

import br.com.luizgustavosgobi.simpleServer.core.annotation.AnnotationDefinition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationProcessorScanner {
    Map<Class<? extends Annotation>, AnnotationDefinition<?>> annotationProcessors = new HashMap<>();

    @SuppressWarnings("unchecked")
    public List<AnnotationDefinition<?>> scan(Object... args) {
        if (args.length == 0)
            throw new IllegalArgumentException("Annotation Processor Scan deed the class list arg to scan!");

        List<Class<?>> classes = new ArrayList<>();
        if (args[0] instanceof List<?>)
            classes.addAll((List<Class<?>>) args[0]);

        for (Class<?> clazz : classes) {
            checkAndAdd(clazz);
        }

        return new ArrayList<>(annotationProcessors.values());
    }

    private boolean isProcessorClass(Class<?> clazz) {
        return AnnotationDefinition.class.isAssignableFrom(clazz)
                && !clazz.isInterface()
//                && !Modifier.isAbstract(clazz.getModifiers())
//                && clazz.getSimpleName().equals("Handler")
//                && clazz.getEnclosingClass() != null
                && clazz.getEnclosingClass().isAnnotation();
    }

    @SuppressWarnings("unchecked")
    private void checkAndAdd(Class<?> clazz) {
        if (isProcessorClass(clazz)) {
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) clazz.getEnclosingClass();
            Class<? extends AnnotationDefinition<?>> processorClass = (Class<? extends AnnotationDefinition<?>>) clazz;

            AnnotationDefinition<?> instance;
            try {
                instance = processorClass.getConstructor().newInstance();
            } catch (Exception _) {
                return;
            }

            annotationProcessors.putIfAbsent(annotationClass, instance);
        }
    }
}
