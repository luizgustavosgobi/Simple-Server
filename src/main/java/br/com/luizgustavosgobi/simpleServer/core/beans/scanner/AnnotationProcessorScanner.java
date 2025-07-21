package br.com.luizgustavosgobi.simpleServer.core.beans.scanner;

import br.com.luizgustavosgobi.simpleServer.core.beans.processor.AnnotationProcessor;
import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Scanner para encontrar todas as classes que implementam AnnotationProcessor
 * em todo o classpath, incluindo dependências externas
 */
public class AnnotationProcessorScanner {

    /**
     * Encontra todas as classes que implementam AnnotationProcessor no classpath
     * @return Lista de classes que implementam AnnotationProcessor
     */
    public List<Class<? extends AnnotationProcessor<?>>> findAllAnnotationProcessors() {
        List<Class<? extends AnnotationProcessor<?>>> processors = new ArrayList<>();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String[] classpathEntries = System.getProperty("java.class.path").split(File.pathSeparator);

            for (String classpathEntry : classpathEntries) {
                File file = new File(classpathEntry);
                if (file.isDirectory()) {
                    scanDirectory(file, "", processors, classLoader);
                } else if (file.getName().endsWith(".jar")) {
                    scanJarFile(file, processors, classLoader);
                }
            }

            scanPackagesUsingReflection(processors);

        } catch (Exception e) {
            Logger.Error(this, "Error to process AnnotationProcessors: " + e.getMessage());
        }

        return processors;
    }

    /**
     * Encontra todas as anotações que possuem classes Handler internas que implementam AnnotationProcessor
     * @return Mapa de anotação -> processador
     */
    public Map<Class<? extends Annotation>, Class<? extends AnnotationProcessor<?>>> findAnnotationsWithProcessors() {
        Map<Class<? extends Annotation>, Class<? extends AnnotationProcessor<?>>> annotationProcessorMap = new HashMap<>();

        try {
            List<Class<? extends AnnotationProcessor<?>>> processors = findAllAnnotationProcessors();

            for (Class<? extends AnnotationProcessor<?>> processorClass : processors) {
                try {
                    if (processorClass.getSimpleName().equals("Handler") &&
                        processorClass.getEnclosingClass() != null &&
                        processorClass.getEnclosingClass().isAnnotation()) {

                        @SuppressWarnings("unchecked")
                        Class<? extends Annotation> annotationClass =
                            (Class<? extends Annotation>) processorClass.getEnclosingClass();

                        annotationProcessorMap.put(annotationClass, processorClass);
                    }
                } catch (Exception e) {
                    Logger.Error(this, "Error to process the class: " + processorClass.getName());
                }
            }

        } catch (Exception e) {
            Logger.Error(this, "Error to map annotations: " + e.getMessage());
        }

        return annotationProcessorMap;
    }

    private void scanDirectory(File directory, String packageName, List<Class<? extends AnnotationProcessor<?>>> processors, ClassLoader classLoader) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                scanDirectory(file, subPackage, processors, classLoader);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." +
                    file.getName().substring(0, file.getName().length() - 6);
                checkClass(className, processors, classLoader);
            }
        }
    }

    private void scanJarFile(File jarFile, List<Class<? extends AnnotationProcessor<?>>> processors, ClassLoader classLoader) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.contains("$")) {
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    checkClass(className, processors, classLoader);
                }
            }
        } catch (IOException e) {
            Logger.Error("Error to scan JAR " + jarFile.getName() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void checkClass(String className, List<Class<? extends AnnotationProcessor<?>>> processors, ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass(className);

            if (AnnotationProcessor.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                processors.add((Class<? extends AnnotationProcessor<?>>) clazz);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError _) {
        } catch (Exception e) {
            Logger.Error("Error to verify the class " + className + ": " + e.getMessage());
        }
    }

    private void scanPackagesUsingReflection(List<Class<? extends AnnotationProcessor<?>>> processors) {
        String[] knownPackages = {
            "br.com.luizgustavosgobi"
        };

        ClassScanner scanner = new ClassScanner();

        for (String packageName : knownPackages) {
            try {
                List<Class<?>> classes = scanner.findClassesInPackage(packageName);

                for (Class<?> clazz : classes) {
                    if (AnnotationProcessor.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {

                        @SuppressWarnings("unchecked")
                        Class<? extends AnnotationProcessor<?>> processorClass =
                            (Class<? extends AnnotationProcessor<?>>) clazz;

                        if (!processors.contains(processorClass)) {
                            processors.add(processorClass);
                        }
                    }
                }
            } catch (Exception _) {}
        }
    }
}
