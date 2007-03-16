package org.trinkets.util.jni;

import org.jetbrains.annotations.NotNull;
import sun.misc.Resource;
import sun.misc.URLClassPath;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * JNI Class Loader help to load custom JNI libraries without any puting
 * native libraries into ${java.library.path} location.
 *
 * @author Alexey Efimov
 */
public class JNIClassLoader extends SecureClassLoader {
    private final String libraryExtension;
    private final File libraryCacheDirectory;
    private static final String MANIFEST_URL = "META-INF/MANIFEST.MF";

    public JNIClassLoader(@NotNull ClassLoader parent, @NotNull String libraryExtension, @NotNull File libraryCacheDirectory) {
        super(parent);
        this.libraryExtension = libraryExtension;
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

    protected String findLibrary(String libname) {
        String libraryFileName = libname.concat(libraryExtension);
        File libraryFile = new File(libraryCacheDirectory, libraryFileName);
        if (libraryFile.exists() && libraryFile.isFile()) {
            return libraryFile.getAbsolutePath();
        }
        return super.findLibrary(libname);
    }

    /**
     * This extract resource from classpath to temporary file.
     *
     * @param bundleURL Resource URL of bundled libraries ZIP archive
     * @throws java.io.IOException For IO operations error
     */
    public final void deployBundle(@NotNull URL bundleURL) throws IOException {
        if (!libraryCacheDirectory.exists()) {
            libraryCacheDirectory.mkdirs();
            libraryCacheDirectory.deleteOnExit();
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
            if (platform != null && System.getProperty("os.name", "").toLowerCase().contains(platform.toLowerCase())) {
                // Unpack library
                Resource resource = ucp.getResource(entryName);
                if (resource != null) {
                    String libraryName = entryName;
                    int si = libraryName.lastIndexOf('/');
                    if (si != -1) {
                        libraryName = libraryName.substring(si + 1);
                    }
                    if (!libraryName.endsWith(libraryExtension)) {
                        libraryName = libraryName.concat(libraryExtension);
                    }
                    File file = new File(libraryCacheDirectory, libraryName);
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
