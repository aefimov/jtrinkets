package org.trinkets.util.jni;

import org.jetbrains.annotations.NotNull;

/**
 * Platform info.
 *
 * @author Alexey Efimov
 */
final class PlatformInfo {
    public static final String OS_NAME = System.getProperty("os.name", "").toLowerCase();
    public static final boolean OS_WINDOWS = OS_NAME.startsWith("windows");
    public static final boolean OS_MAC = OS_NAME.startsWith("mac");
    public static final String JNI_LIBRARY_NAMING_FORMAT = OS_WINDOWS ? "{0}.dll" : (OS_MAC ? "lib{0}.jnilib" : "lib{0}.so");

    public static boolean isMatched(@NotNull String platform) {
        return OS_NAME.contains(platform.toLowerCase());
    }

    private PlatformInfo() {
    }
}
