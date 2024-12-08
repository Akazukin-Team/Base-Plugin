package org.akazukin.library.utils;

import org.jetbrains.annotations.Nullable;

public class ObjectUtils {
    @Nullable
    public static Boolean getBoolean(final Object bool) {
        if (bool == null) return null;
        if (bool instanceof String) {
            if (((String) bool).equalsIgnoreCase("true")) return true;
            else if (((String) bool).equalsIgnoreCase("false")) return false;
        } else if (bool instanceof Byte) {
            if (bool.equals((byte) 0)) return false;
            else if (bool.equals((byte) 1)) return true;
        }
        return null;
    }

    @Nullable
    public static Byte toByte(final Boolean bool) {
        return bool != null ? (byte) (bool ? 1 : 0) : null;
    }
}
