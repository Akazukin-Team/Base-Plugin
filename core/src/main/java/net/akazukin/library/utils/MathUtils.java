package net.akazukin.library.utils;

public class MathUtils {
    public static boolean contains(int value, int range, int range2) {
        return Math.min(range, range2) <= value && value <= Math.max(range, range2);
    }
}
