package org.trinkets.util.jni.annotations;

import java.lang.annotation.*;

/**
 * JNI bundle annotation. This annotation link JNI implementation clas with a packed JNI bundle.
 *
 * @author Alexey Efimov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JNIBundle {
    String value();
}
