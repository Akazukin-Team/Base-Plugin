package net.akazukin.library.utils;

public class MathUtils {
    public static boolean contains(final int value, final int range, final int range2) {
        return Math.min(range, range2) <= value && value <= Math.max(range, range2);
    }

    public static int clamp(final int value, final int range, final int range2) {
        return Math.min(Math.max(value, Math.min(range, range2)), Math.max(range, range2));
    }
}
