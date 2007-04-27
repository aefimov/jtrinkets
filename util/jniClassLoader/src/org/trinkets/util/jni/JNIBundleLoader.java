package org.trinkets.util.jni;

import org.jetbrains.annotations.NotNull;
import org.trinkets.util.jni.annotations.JNIBundle;
import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.reflect.Reflection;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * JNI libraries bundle loader.
 *
 * @author Alexey Efimov
 */
public final class JNIBundleLoader {
    private static final String MANIFEST_URL = "META-INF/MANIFEST.MF";
    private static final String PLATFORM = System.getProperty("os.name", "").toLowerCase();

    private final File cacheDirectory;
    private final String libraryNamingFormat;

    public JNIBundleLoader(@NotNull File cacheDirectory) {
        this(cacheDirectory, getLibraryNamingFormat());
    }

    private static String getLibraryNamingFormat() {
        if (PLATFORM.startsWith("windows")) {
            return "{0}.dll";
        }
        if (PLATFORM.startsWith("mac")) {
            return "lib{0}.jnilib";
        }
        return "lib{0}.so";
    }

    public JNIBundleLoader(@NotNull File cacheDirectory, @NotNull String libraryNamingFormat) {
        this.cacheDirectory = cacheDirectory;
        this.libraryNamingFormat = libraryNamingFormat;
    }

    @NotNull
    public final <T> T newJNI(@NotNull Class<? extends T> implementationClass) throws IllegalAccessException, InstantiationException {
        return predefineJNIAnnotatedClass(implementationClass).newInstance();
    }

    /**
     * Load JNI library via special class loader.
     *
     * @param implementationClass String reference path to implementation of JNI bundled class. Please do not refer class directly via {@link Class} object to get name, it caused class to be loaded into wrong classloader.
     * @return Loaded class
     */
    @NotNull
    public final <T> Class<T> predefineJNIAnnotatedClass(@NotNull Class<? extends T> implementationClass) {
        return predefineJNIAnnotatedClass(Reflection.getCallerClass(2).getClassLoader(), implementationClass);
    }


    /**
     * Load JNI library via special class loader.
     *
     * @param parentClassLoader   Parent classloader (current classloader)
     * @param implementationClass String reference path to implementation of JNI bundled class. Please do not refer class directly via {@link Class} object to get name, it caused class to be loaded into wrong classloader.
     * @return Loaded class
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public final <T> Class<T> predefineJNIAnnotatedClass(ClassLoader parentClassLoader, Class<? extends T> implementationClass) {
        JNIClassLoader jniLoader = new JNIClassLoader(parentClassLoader, libraryNamingFormat, cacheDirectory);
        try {
            Class<T> jniType = (Class<T>) jniLoader.predefineClass(implementationClass.getName());
            JNIBundle bundle = jniType.getAnnotation(JNIBundle.class);
            if (bundle != null) {
                String[] bundleURLs = bundle.value();
                if (bundleURLs != null) {
                    for (String bundleURL : bundleURLs) {
                        URL resource = jniType.getResource(bundleURL);
                        if (resource == null) {
                            throw new FileNotFoundException("the JNI bundle not found in classpath: " + bundleURL);
                        }
                        // Deploy bundles
                        deployBundle(resource, cacheDirectory);
                    }
                }
            }
            // Load linked libraries
            jniLoader.loadLibraries(jniType);

            return jniType;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This extract resource from classpath to temporary file.
     *
     * @param bundleURL Resource URL of bundled libraries ZIP archive
     * @param dir       Deploy directory
     * @throws java.io.IOException For IO operations error
     */
    private static void deployBundle(@NotNull URL bundleURL, File dir) throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
            dir.deleteOnExit();
        }
        URLClassPath ucp = new URLClassPath(new URL[]{bundleURL});
        Resource manifestResource = ucp.getResource(MANIFEST_URL);
        if (manifestResource == null) {
            throw new IllegalArgumentException(MessageFormat.format("The jar is not JNI bundle (missing manifest): {0}", bundleURL.getPath()));
        }
        Manifest manifest = manifestResource.getManifest();
        Map<String, Attributes> manifestEntries = manifest.getEntries();
        for (String entryName : manifestEntries.keySet()) {
            Attributes attributes = manifestEntries.get(entryName);
            String platform = attributes.getValue("Platform");
            if (platform != null && PLATFORM.contains(platform.toLowerCase())) {
                // Unpack library
                Resource resource = ucp.getResource(entryName);
                if (resource != null) {
                    String libraryName = entryName;
                    int si = libraryName.lastIndexOf('/');
                    if (si != -1) {
                        libraryName = libraryName.substring(si + 1);
                    }
                    File file = new File(dir, libraryName);
                    file.deleteOnExit();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    InputStream inputStream = new BufferedInputStream(resource.getInputStream(), 1024);
                    try {
                        byte[] buffer = new byte[1024];
                        for (int n = inputStream.read(buffer); n != -1; n = inputStream.read(buffer)) {
                            fileOutputStream.write(buffer, 0, n);
                        }
                    } finally {
                        inputStream.close();
                        fileOutputStream.close();
                    }
                }
            }
        }
    }
}
