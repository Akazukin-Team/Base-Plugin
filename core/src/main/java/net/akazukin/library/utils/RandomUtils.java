package net.akazukin.library.utils;

import java.util.Random;

public class RandomUtils {
    public static final Random RANDOM = new Random();

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static int nextInt(final int i1, final int i2) {
        return RANDOM.nextInt(Math.max(i1, i2) - Math.min(i1, i2)) + Math.min(i1, i2);
    }

    public static double nextDouble(final double d1, final double d2) {
        return (RANDOM.nextDouble() * (Math.max(d1, d2) - Math.min(d1, d2))) + Math.min(d1, d2);
    }

    public static float nextFloat(final float f1, final float f2) {
        return (RANDOM.nextFloat() * (Math.max(f1, f2) - Math.min(f1, f2))) + Math.min(f1, f2);
    }

    public static String randomString(final String chars, final int length) {
        final StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final int i2 = nextInt(0, chars.length() - 1);
            str.append(chars.charAt(i2));
        }
        return str.toString();
    }
}
