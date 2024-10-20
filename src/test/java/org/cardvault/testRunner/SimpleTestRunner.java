package org.cardvault.testRunner;

import org.cardvault.core.logging.Logger;
import org.cardvault.testRunner.annotations.Test;
import org.cardvault.testRunner.annotations.TestClass;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SimpleTestRunner {

    public static void main(String[] args) {
        Logger.info("Running all tests...");
        runAllTests("org.cardvault");
    }

    public static List<Class<?>> findAllTestClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');

        URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (packageUrl != null) {
            File packageDir = new File(packageUrl.getFile());
            findClassesInDirectory(packageDir, packageName, classes);
        }
        return classes;
    }

    private static void findClassesInDirectory(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.getName().endsWith(".class")) {
                    try {
                        String className = packageName + "." + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void runTests(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(TestClass.class)) {
            return;
        }

        Method[] methods = testClass.getDeclaredMethods();
        int passedTests = 0;
        int failedTests = 0;

        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                Logger.info("Running test: " + method.getName());
                try {
                    method.invoke(testClass.getDeclaredConstructor().newInstance());
                    Logger.success("Test passed: " + method.getName());
                    passedTests++;
                } catch (Exception e) {
                    Logger.fail("Test failed: " + method.getName() + " - " + e.getCause());
                    failedTests++;
                }
            }
        }
        Logger.info("-----------------------------");
        Logger.info("Test class: " + testClass.getName());
        Logger.info("Tests passed: " + passedTests + ", Tests failed: " + failedTests);
        Logger.info("-----------------------------");
    }

    public static void runAllTests(String packageName) {
        List<Class<?>> testClasses = findAllTestClasses(packageName);
        for (Class<?> testClass : testClasses) {
            runTests(testClass);
        }
    }
}
