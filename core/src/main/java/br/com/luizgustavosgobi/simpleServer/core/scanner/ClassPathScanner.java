package br.com.luizgustavosgobi.simpleServer.core.scanner;

import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathScanner {
    private static final Map<String, List<Class<?>>> packageClassCache = new ConcurrentHashMap<>();
    private static final String CLASS_INDEX_RESOURCE = "META-INF/simple-server.class-index";

    public List<Class<?>> scan(List<String> packages) {
        for (String packageName : packages) {
            packageClassCache.computeIfAbsent(packageName, this::scanPackage);
        }

        return packageClassCache
                .values()
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private List<Class<?>> scanPackage(String packageName) {
        try {
            List<Class<?>> classes = new ArrayList<>();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("jar".equals(protocol)) {
                    classes.addAll(findClassesInJar(resource, packageName));
                    continue;
                }

                if ("file".equals(protocol)) {
                    File directory = new File(resource.toURI());
                    classes.addAll(findClassesInDirectory(directory, packageName));
                    continue;
                }

                Logger.Debug("Unsupported classpath protocol for package " + packageName + ": " + protocol);
            }

            if (classes.isEmpty()) {
                classes.addAll(findClassesFromIndex(classLoader, packageName));
            }

            return classes;
        } catch (Exception e) {
            Logger.Error(this, "Error searching for classes in package " + packageName + ": " + e.getMessage());
            return findClassesFromIndex(Thread.currentThread().getContextClassLoader(), packageName);
        }
    }

    private List<Class<?>> findClassesInDirectory(File directory, String packageName) {
        if (!directory.exists()) return Collections.emptyList();

        List<Class<?>> classes = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    addClassIfPresent(classes, className, "Could not load class: ");
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
                    addClassIfPresent(classes, className, "Could not load class from JAR: ");
                }
            }
        }

        return classes;
    }

    private List<Class<?>> findClassesFromIndex(ClassLoader classLoader, String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        try {
            Enumeration<URL> indexResources = classLoader.getResources(CLASS_INDEX_RESOURCE);

            while (indexResources.hasMoreElements()) {
                URL indexUrl = indexResources.nextElement();
                try (InputStream input = indexUrl.openStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String className = line.trim();
                        if (className.isEmpty() || className.startsWith("#")) {
                            continue;
                        }

                        if (className.startsWith(packageName + ".") || className.startsWith(packageName + "$")) {
                            addClassIfPresent(classes, className, "Could not load indexed class: ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.Debug("Could not load class index " + CLASS_INDEX_RESOURCE + ": " + e.getMessage());
        }

        return classes;
    }

    private void addClassIfPresent(List<Class<?>> classes, String className, String errorMessagePrefix) {
        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            Logger.Debug(errorMessagePrefix + className);
        }
    }
}