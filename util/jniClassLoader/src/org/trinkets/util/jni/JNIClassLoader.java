package org.trinkets.util.jni;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.trinkets.util.jni.annotations.JNILibrary;
import sun.reflect.Reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.text.MessageFormat;
import java.util.*;

/**
 * JNI Class Loader help to load custom JNI libraries without any puting native libraries into ${java.library.path}
 * location.
 *
 * @author Alexey Efimov
 */
public class JNIClassLoader extends SecureClassLoader {
    private static final Method LOAD_LIBRARY_METHOD;

    static {
        try {
            LOAD_LIBRARY_METHOD = ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, boolean.class);
            LOAD_LIBRARY_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final String libraryNamingFormat;
    private final File libraryCacheDirectory;
    private final Map<String, Class<?>> definedClasses = new HashMap<String, Class<?>>();
    private final Set<String> loadedLibraries = new HashSet<String>();

    public JNIClassLoader(@NotNull File libraryCacheDirectory) {
        this((String) null, libraryCacheDirectory);
    }

    public JNIClassLoader(@NonNls String libraryNamingFormat, @NotNull File libraryCacheDirectory) {
        this(Reflection.getCallerClass(2).getClassLoader(), libraryNamingFormat, libraryCacheDirectory);
    }

    public JNIClassLoader(@NotNull ClassLoader parent, @NotNull File libraryCacheDirectory) {
        this(parent, null, libraryCacheDirectory);
    }

    public JNIClassLoader(@NotNull ClassLoader parent, @NonNls String libraryNamingFormat, @NotNull File libraryCacheDirectory) {
        super(parent);
        this.libraryNamingFormat = libraryNamingFormat != null ? libraryNamingFormat : PlatformInfo.JNI_LIBRARY_NAMING_FORMAT;
        this.libraryCacheDirectory = libraryCacheDirectory;
        if (libraryCacheDirectory.exists() && !libraryCacheDirectory.isDirectory()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("The cache directory path is point to a file: {0}", libraryCacheDirectory.getAbsolutePath()));
        }
    }

    /**
     * Create via reflection JNI wrapped object. This method can throw runtime-exceptions (wrapped exeption from
     * reflection calls).
     *
     * @param implementationClass Implementation of JNI wrapped class
     * @param parameters          Parameters passed to constructor
     * @return Return created via reflection JNI class object
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public final <T> T newJNI(@NotNull Class<? extends T> implementationClass, Object... parameters) {
        return newJNI(implementationClass, null, parameters);
    }

    /**
     * Create via reflection JNI wrapped object. This method can throw runtime-exceptions (wrapped exeption from
     * reflection calls).
     *
     * @param implementationClass Implementation of JNI wrapped class
     * @param parameters          Parameters passed to constructor
     * @param listenner           Listenner for predefine operation. If class redefined this monitor will be notified.
     * @return Return created via reflection JNI class object
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    final <T> T newJNI(@NotNull Class<? extends T> implementationClass, JNIClassLoaderListenner listenner, Object... parameters) {
        try {
            Class<?> jni = predefineClass(implementationClass.getName(), listenner);
            loadLibraries(jni);
            if (parameters.length == 0) {
                return (T) jni.newInstance();
            } else {
                Class[] types = new Class[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    types[i] = parameters[i].getClass();
                }
                Constructor<T> constructor = (Constructor<T>) jni.getDeclaredConstructor(types);
                return constructor.newInstance(parameters);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assign class to be loaded via this classloader.
     *
     * @param name Full name of class
     * @return Class instance
     * @throws ClassNotFoundException if class not found
     */
    @NotNull
    public final Class<?> predefineClass(@NotNull String name) throws ClassNotFoundException {
        return predefineClass(name, null);
    }

    /**
     * Assign class to be loaded via <b>this</b> classloader.
     *
     * @param name      Full name of class
     * @param listenner Listenner for predefine operation. If class redefined this monitor will be notified.
     * @return Class instance
     * @throws ClassNotFoundException if class not found
     */
    @NotNull
    final Class<?> predefineClass(@NotNull String name, JNIClassLoaderListenner listenner) throws ClassNotFoundException {
        Class<?> definedClass = definedClasses.get(name);
        if (definedClass != null) {
            // Already defined
            return definedClass;
        }
        // Try to delegate
        URL classURL = getResource(name.replace('.', '/').concat(".class"));
        if (classURL != null) {
            try {
                ReadableByteChannel ch = Channels.newChannel(classURL.openStream());
                try {
                    List<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
                    ByteBuffer buffer;
                    try {
                        int size = 0;
                        int readed;
                        while ((readed = ch.read(buffer = ByteBuffer.allocate(1024))) > 0) {
                            buffers.add((ByteBuffer) buffer.flip());
                            size += readed;
                        }
                        if (buffers.size() == 1) {
                            buffer = buffers.get(0);
                        } else {
                            buffer = ByteBuffer.allocate(size);
                            for (ByteBuffer trunk : buffers) {
                                buffer.put(trunk);
                            }
                        }
                        Class<?> aClass = defineClass(name, buffer, (ProtectionDomain) null);
                        definedClasses.put(name, aClass);
                        if (listenner != null) {
                            listenner.classPredefined(aClass);
                        }
                        return aClass;
                    } finally {
                        buffers.clear();
                    }
                } finally {
                    ch.close();
                }
            } catch (IOException e) {
                // Ignore
            }
        }
        return super.findClass(name);
    }

    public void loadLibraries(Class<?> jniType) throws IllegalAccessException, InvocationTargetException {
        // Load libraries
        if (jniType.isAnnotationPresent(JNILibrary.class)) {
            JNILibrary library = jniType.getAnnotation(JNILibrary.class);
            for (String lib : library.value()) {
                if (!loadedLibraries.contains(lib)) {
                    LOAD_LIBRARY_METHOD.invoke(null, jniType, lib, false);
                    loadedLibraries.add(lib);
                }
            }
        }
    }

    protected String findLibrary(String libname) {
        String libraryFileName = MessageFormat.format(libraryNamingFormat, libname);
        File libraryFile = new File(libraryCacheDirectory, libraryFileName);
        if (libraryFile.exists() && libraryFile.isFile()) {
            return libraryFile.getAbsolutePath();
        }
        return super.findLibrary(libname);
    }

    public final File getLibraryCacheDirectory() {
        return libraryCacheDirectory;
    }
}
