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
    }

    @SuppressWarnings({"unchecked"})
    public void testClassLoader() throws IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException {
        String userHome = System.getProperty("user.home");
        File librariesDir = new File(userHome, ".jni_cache");

        JNIClassLoader jniClassLoader = new JNIClassLoader(TestJNIHelloWorld.class.getClassLoader(), "{0}.dll", librariesDir);
        Class<JNIHelloWorld> jniHelloWorld = (Class<JNIHelloWorld>) jniClassLoader.predefineClass(JNIHelloWorldImpl.class.getName());
        jniClassLoader.loadLibraries(jniHelloWorld);
        jniHelloWorld.newInstance().sayHello("Hello JNI World!");
    }
}
