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
    private static JNIClassLoader LOADER = new JNIClassLoader(new File(System.getProperty("user.home"), ".jni_cache"));

    public void testBundleLoader() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        JNIBundleLoader bundleLoader = new JNIBundleLoader(LOADER);
        JNIHelloWorld jniHelloWorld = bundleLoader.newJNI(JNIHelloWorldImpl.class, "bob");
        jniHelloWorld.sayHello("Hello JNI World!");
        JNIHelloWorld jniHelloWorld2 = bundleLoader.newJNI(JNIHelloWorldImpl.class, "sam");
        jniHelloWorld2.sayHello("Hello JNI World!");
    }

    @SuppressWarnings({"unchecked"})
    public void testClassLoader() throws IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        JNIHelloWorld jniHelloWorld = LOADER.newJNI(JNIHelloWorldImpl.class, "bob");
        jniHelloWorld.sayHello("Hello JNI World!");

        JNIHelloWorld jniHelloWorld2 = LOADER.newJNI(JNIHelloWorldImpl.class, "sam");
        jniHelloWorld2.sayHello("Hello JNI World!");
    }
}
