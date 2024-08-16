package net.akazukin.library.utils;

public class MathUtils {
    public static boolean contains(final int value, final int range, final int range2) {
        return Math.min(range, range2) <= value && value <= Math.max(range, range2);
    }

    public static int clamp(final int value, final int range, final int range2) {
        return Math.min(Math.max(value, Math.min(range, range2)), Math.max(range, range2));
    }

    public static double clamp(final double value, final double range, final double range2) {
        return Math.min(Math.max(value, Math.min(range, range2)), Math.max(range, range2));
    }

    public static float closer(final float value, final float base, final float percent) {
        return base - ((base - value) * percent);
    }

    public static double closer(final double value, final double base, final double percent) {
        return base - ((base - value) * percent);
    }
}
