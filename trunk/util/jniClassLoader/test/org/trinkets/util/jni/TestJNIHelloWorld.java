package org.trinkets.util.jni;

import junit.framework.TestCase;

import java.io.File;

/**
 * Test for {@link org.trinkets.util.jni.JNIHelloWorld}
 *
 * @author Alexey Efimov
 */
public class TestJNIHelloWorld extends TestCase {
    public void testLoadingJNI() throws IllegalAccessException, InstantiationException {
        String directory = System.getProperty("user.home");

        JNIBundleLoader bundleLoader = new JNIBundleLoader(new File(directory, ".jni_cache"), ".dll");
        Class<?> jniClass = bundleLoader.loadClass("org.trinkets.util.jni.JNIHelloWorldImpl");
        JNIHelloWorld jniHelloWorld = (JNIHelloWorld) jniClass.newInstance();
        jniHelloWorld.sayHello("Hello JNI World!");
    }
}
