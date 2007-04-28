package org.trinkets.util.jni;

import junit.framework.TestCase;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Test for {@link org.trinkets.util.jni.JNIHelloWorld}
 *
 * @author Alexey Efimov
 */
public class TestJNIHelloWorld extends TestCase {
    public void testBundleLoader() throws IllegalAccessException, InstantiationException {
        String userHome = System.getProperty("user.home");
        File librariesDir = new File(userHome, ".jni_cache");

        JNIBundleLoader bundleLoader = new JNIBundleLoader(librariesDir);
        JNIHelloWorld jniHelloWorld = bundleLoader.newJNI(JNIHelloWorldImpl.class);
        jniHelloWorld.sayHello("Hello JNI World!");
        JNIHelloWorld jniHelloWorld2 = bundleLoader.newJNI(JNIHelloWorldImpl.class);
        jniHelloWorld2.sayHello("Hello JNI World!");
    }

    @SuppressWarnings({"unchecked"})
    public void testClassLoader() throws IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException {
        String userHome = System.getProperty("user.home");
        File librariesDir = new File(userHome, ".jni_cache");

        JNIClassLoader jniClassLoader = new JNIClassLoader(librariesDir);
        JNIHelloWorld jniHelloWorld = jniClassLoader.newJNI(JNIHelloWorldImpl.class);
        jniHelloWorld.sayHello("Hello JNI World!");

        JNIHelloWorld jniHelloWorld2 = jniClassLoader.newJNI(JNIHelloWorldImpl.class);
        jniHelloWorld2.sayHello("Hello JNI World!");
    }
}
