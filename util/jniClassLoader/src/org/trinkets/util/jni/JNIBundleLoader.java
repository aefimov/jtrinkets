package org.trinkets.util.jni;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.trinkets.util.jni.annotations.JNIBundle;
import sun.misc.Resource;
import sun.misc.URLClassPath;
import sun.reflect.Reflection;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * JNI libraries bundle loader.
 *
 * @author Alexey Efimov
 */
public class JNIBundleLoader {
    private final Set<URL> deployedBundles = new HashSet<URL>();
    private final JNIClassLoader jniClassLoader;
    private final JNIClassLoaderListenner listenner = new JNIClassLoaderListennerImpl();

    public JNIBundleLoader(@NotNull File libraryCacheDirectory) {
        this((String) null, libraryCacheDirectory);
    }

    public JNIBundleLoader(@NonNls String libraryNamingFormat, @NotNull File libraryCacheDirectory) {
        this(Reflection.getCallerClass(2).getClassLoader(), libraryNamingFormat, libraryCacheDirectory);
    }

    public JNIBundleLoader(@NotNull ClassLoader parent, @NotNull File libraryCacheDirectory) {
        this(parent, null, libraryCacheDirectory);
    }

    public JNIBundleLoader(@NotNull ClassLoader parent, @NonNls String libraryNamingFormat, @NotNull File libraryCacheDirectory) {
        this(new JNIClassLoader(parent, libraryNamingFormat, libraryCacheDirectory));
    }

    public JNIBundleLoader(@NotNull JNIClassLoader jniClassLoader) {
        this.jniClassLoader = jniClassLoader;
    }

    @NotNull
    public final <T> T newJNI(@NotNull Class<? extends T> implementationClass, Object... parameters) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return jniClassLoader.newJNI(implementationClass, listenner, parameters);
    }

    /**
     * Load JNI library via special class loader.
     *
     * @param implementationClass String reference path to implementation of JNI bundled class. Please do not refer
     *                            class directly via {@link Class} object to get name, it caused class to be loaded into
     *                            wrong classloader.
     * @return Loaded class
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public final <T> Class<T> predefineClass(Class<? extends T> implementationClass) {
        try {
            return (Class<T>) jniClassLoader.predefineClass(implementationClass.getName(), listenner);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void deployBundles(Class<?> jniType) throws IOException {
        JNIBundle bundle = jniType.getAnnotation(JNIBundle.class);
        if (bundle != null) {
            String[] bundleURLs = bundle.value();
            if (bundleURLs != null) {
                for (String bundleURL : bundleURLs) {
                    URL resource = jniType.getResource(bundleURL);
                    if (resource == null) {
                        throw new FileNotFoundException("the JNI bundle not found in classpath: " + bundleURL);
                    } else if (!deployedBundles.contains(resource)) {
                        // Deploy bundles
                        deployBundle(resource, jniClassLoader.getLibraryCacheDirectory());
                        deployedBundles.add(resource);
                    }
                }
            }
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
        Resource manifestResource = ucp.getResource("META-INF/MANIFEST.MF");
        if (manifestResource == null) {
            throw new IllegalArgumentException(MessageFormat.format("The jar is not JNI bundle (missing manifest): {0}", bundleURL.getPath()));
        }
        Manifest manifest = manifestResource.getManifest();
        Map<String, Attributes> manifestEntries = manifest.getEntries();
        for (String entryName : manifestEntries.keySet()) {
            Attributes attributes = manifestEntries.get(entryName);
            String platform = attributes.getValue("Platform");
            if (platform != null && PlatformInfo.isMatched(platform)) {
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

    private class JNIClassLoaderListennerImpl implements JNIClassLoaderListenner {
        public void classPredefined(Class<?> jniType) {
            // Now deploy all bundles defined in class
            try {
                deployBundles(jniType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
