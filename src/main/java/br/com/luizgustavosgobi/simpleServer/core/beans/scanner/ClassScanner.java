package br.com.luizgustavosgobi.simpleServer.core.beans.scanner;

import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassScanner {
    private static final Map<String, List<Class<?>>> packageClassCache = new ConcurrentHashMap<>();

    public List<Class<?>> findClassesInPackage(String basePackage) {
        return packageClassCache.computeIfAbsent(basePackage, pkg -> {
            try { return scanClassesInPackage(pkg);}
            catch (Exception e) {
                Logger.Error(this, "Error searching for classes in package " + pkg + ": " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }

    private List<Class<?>> scanClassesInPackage(String basePackage) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<Class<?>> classes = new ArrayList<>();

        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                findClassesInDirectory(directory, basePackage, classes);
            }
        }

        return classes;
    }

    private void findClassesInDirectory(File directory, String packageName, List<Class<?>> classes) {
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), classes);
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
    }
}