package org.trinkets.util.jni.annotations;

import java.lang.annotation.*;

/**
 * JNI libraries. This annotation point to libraries must be loaded.
 *
 * @author Alexey Efimov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JNILibraries {
    String[] value();
}
