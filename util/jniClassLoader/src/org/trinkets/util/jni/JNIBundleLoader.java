package org.trinkets.util.jni;

import org.jetbrains.annotations.NotNull;
import org.trinkets.util.jni.annotations.JNIBundle;
import org.trinkets.util.jni.annotations.JNILibraries;
import sun.reflect.Reflection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;

/**
 * JNI libraries bundle loader.
 *
 * @author Alexey Efimov
 */
public final class JNIBundleLoader {
    private final File cacheDirectory;
    private final String extension;

    public JNIBundleLoader(@NotNull File cacheDirectory, @NotNull String extension) {
        this.cacheDirectory = cacheDirectory;
        this.extension = extension;
    }

    /**
     * Load JNI library via special class loader.
     *
     * @param jniClass String reference path to implementation of JNI bundled class. Please do not refer class directly via {@link Class} object to get name, it caused class to be loaded into wrong classloader.
     * @return Loaded class
     */
    @NotNull
    public final Class<?> loadClass(@NotNull String jniClass) {
        return loadClass(Reflection.getCallerClass(2).getClassLoader(), jniClass);
    }


    /**
     * Load JNI library via special class loader.
     *
     * @param parentClassLoader Parent classloader (current classloader)
     * @param jniClass          String reference path to implementation of JNI bundled class. Please do not refer class directly via {@link Class} object to get name, it caused class to be loaded into wrong classloader.
     * @return Loaded class
     */
    @SuppressWarnings({"ReflectionForUnavailableAnnotation"})
    @NotNull
    public final Class<?> loadClass(ClassLoader parentClassLoader, @NotNull String jniClass) {
        JNIClassLoader jniLoader = new JNIClassLoader(parentClassLoader, extension, cacheDirectory);
        try {
            Class<?> jniType = jniLoader.predefineClass(jniClass);
            JNIBundle bundle = getAnnotation(jniType, JNIBundle.class);
            String bundleURL = bundle.value();
            URL resource = jniType.getResource(bundleURL);
            if (resource == null) {
                throw new FileNotFoundException("the JNI bundle not found in classpath: " + bundleURL);
            }

            jniLoader.deployBundle(resource);
            // Load libraries
            if (jniType.isAnnotationPresent(JNILibraries.class)) {
                Method method = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, boolean.class);
                method.setAccessible(true);
                JNILibraries libraries = getAnnotation(jniType, JNILibraries.class);
                for (String lib : libraries.value()) {
                    method.invoke(null, jniType, lib, false);
                }
            }
            return jniType;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return annotation from class or throw exception if annotation is not found
     *
     * @param type           Class
     * @param annotationType Annotation class
     * @return Annotation
     */
    @SuppressWarnings({"HardCodedStringLiteral"})
    @NotNull
    public static <T extends Annotation> T getAnnotation(Class<?> type, Class<T> annotationType) {
        T annotation = type.getAnnotation(annotationType);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "class not annotated with {0} annotation", annotationType.getName()
                    )
            );
        }
        return annotation;
    }
}
