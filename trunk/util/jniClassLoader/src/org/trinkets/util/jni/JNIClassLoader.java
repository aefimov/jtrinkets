package org.trinkets.util.jni;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.trinkets.util.jni.annotations.JNILibrary;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * JNI Class Loader help to load custom JNI libraries without any puting
 * native libraries into ${java.library.path} location.
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

    public JNIClassLoader(@NotNull ClassLoader parent, @NotNull @NonNls String libraryNamingFormat, @NotNull File libraryCacheDirectory) {
        super(parent);
        this.libraryNamingFormat = libraryNamingFormat;
        this.libraryCacheDirectory = libraryCacheDirectory;
        if (libraryCacheDirectory.exists() && !libraryCacheDirectory.isDirectory()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("The cache directory path is point to a file: {0}", libraryCacheDirectory.getAbsolutePath()));
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
                        return defineClass(name, buffer, (ProtectionDomain) null);
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
                LOAD_LIBRARY_METHOD.invoke(null, jniType, lib, false);
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

}
