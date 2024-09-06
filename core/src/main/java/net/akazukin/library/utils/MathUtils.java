package net.akazukin.library.utils;

import java.util.Arrays;

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

    public static int max(final int... n) {
        return Arrays.stream(n).max().orElse(Integer.MIN_VALUE);
    }

    public static int min(final int... n) {
        return Arrays.stream(n).min().orElse(Integer.MAX_VALUE);
    }

    public static double max(final double... n) {
        return Arrays.stream(n).max().orElse(Double.MIN_VALUE);
    }

    public static double min(final double... n) {
        return Arrays.stream(n).min().orElse(Double.MAX_VALUE);
    }

    public static float max(final float... n) {
        float max = Float.MIN_VALUE;
        for (final float v : n) {
            max = Math.max(max, v);
        }
        return max;
    }

    public static float min(final float... n) {
        float min = Float.MAX_VALUE;
        for (final float v : n) {
            min = Math.min(min, v);
        }
        return min;
    }
}
