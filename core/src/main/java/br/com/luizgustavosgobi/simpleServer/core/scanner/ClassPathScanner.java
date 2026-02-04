package br.com.luizgustavosgobi.simpleServer.core.scanner;

import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathScanner implements Scanner<List<Class<?>>> {
    private static final Map<String, List<Class<?>>> packageClassCache = new ConcurrentHashMap<>();

    @Override
    public List<Class<?>> scan(Object... args) {
        List<String> knowPackages = new ArrayList<>();

        if (args.length == 0)
            throw new IllegalArgumentException("Class Scanner needs the base package argument!");

        if (args[0] instanceof String basePackage)
            knowPackages.add(basePackage);

        if (args[0] instanceof String[] packages)
            knowPackages.addAll(List.of(packages));

        knowPackages.forEach(basePackage -> {
            packageClassCache.computeIfAbsent(basePackage, pkg -> {
                try {
                    List<Class<?>> classes = new ArrayList<>();
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                    String path = basePackage.replace('.', '/');
                    Enumeration<URL> resources = classLoader.getResources(path);

                    while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        if ("jar".equals(resource.getProtocol())) {
                            classes.addAll(findClassesInJar(resource, basePackage));
                        } else {
                            File directory = new File(resource.toURI());
                            if (directory.exists()) {
                                classes.addAll(Objects.requireNonNull(findClassesInDirectory(directory, basePackage)));
                            }
                        }
                    }

                    return classes;
                }
                catch (Exception e) {
                    Logger.Error(this, "Error searching for classes in package " + pkg + ": " + e.getMessage());
                    return new ArrayList<>();
                }
            });
        });

        return packageClassCache
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Class<?>> findClassesInDirectory(File directory, String packageName) {
        if (!directory.exists()) return null;

        List<Class<?>> classes = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(Objects.requireNonNull(findClassesInDirectory(file, packageName + "." + file.getName())));

                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);

                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        Logger.Debug("Could not load class: " + className);
                    }
                }
            }
        }

        return classes;
    }

    private List<Class<?>> findClassesInJar(URL resource, String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();

        String path = packageName.replace('.', '/');

        JarURLConnection connection = (JarURLConnection) resource.openConnection();
        connection.setUseCaches(true);

        try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(path) && entryName.endsWith(".class") && !entry.isDirectory()) {
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);

                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        Logger.Debug("Could not load class from JAR: " + className);
                    }
                }
            }
        }

        return classes;
    }
}