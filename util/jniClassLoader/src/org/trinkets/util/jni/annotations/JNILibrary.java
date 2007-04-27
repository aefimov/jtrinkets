package org.trinkets.util.jni.annotations;

import java.lang.annotation.*;

/**
 * JNI library (or libraries). This annotation point to library must be loaded.
 * For example, then you define implementation of JNI libeary wrapper you must use this
 * annotation to link class with native library.
 * Then annotated class loaded via {@link org.trinkets.util.jni.JNIClassLoader} all libraries
 * specified for this annotation will be load automaticaly.
 *
 * @author Alexey Efimov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JNILibrary {
    String[] value();
}
