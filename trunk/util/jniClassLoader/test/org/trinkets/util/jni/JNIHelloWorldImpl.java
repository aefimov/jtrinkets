package org.trinkets.util.jni;

import org.trinkets.util.jni.annotations.JNIBundle;
import org.trinkets.util.jni.annotations.JNILibraries;

@JNIBundle("JNIHelloWorldBundle.jar")
@JNILibraries({"JNIHelloWorld"})
public final class JNIHelloWorldImpl implements JNIHelloWorld {
    /**
     * Here is public wrapper to native method.
     *
     * @param hello Hello string
     */
    public void sayHello(String hello) {
        sayHello0(hello);
    }

    /**
     * Here is native method
     *
     * @param hello Hello string
     */
    private native void sayHello0(String hello);
}
