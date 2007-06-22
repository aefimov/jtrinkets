package org.trinkets.util.jni;

/**
 * Listenner for {@link org.trinkets.util.jni.JNIClassLoader}.
 *
 * @author Alexey Efimov
 */
interface JNIClassLoaderListenner {
    void classPredefined(Class<?> jniType);
}
