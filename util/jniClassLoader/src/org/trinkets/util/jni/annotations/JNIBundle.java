package org.trinkets.util.jni.annotations;

import java.lang.annotation.*;

/**
 * JNI bundle annotation. This annotation link JNI implementation class with a packed JNI bundle.
 * Then annotated class loaded via {@link org.trinkets.util.jni.JNIClassLoader} all bundles
 * specified for this annotation will be deployed automaticaly.
 *
 * @author Alexey Efimov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JNIBundle {
    String[] value();
}
