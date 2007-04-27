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
        String userHome = System.getProperty("user.home");
        File librariesDir = new File(userHome, ".jni_cache");

        JNIBundleLoader bundleLoader = new JNIBundleLoader(librariesDir);
        JNIHelloWorld jniHelloWorld = bundleLoader.newJNI(JNIHelloWorldImpl.class);
        jniHelloWorld.sayHello("Hello JNI World!");
    }
}
