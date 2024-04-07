package net.akazukin.library.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    private static final MessageDigest sha3_512;

    static {
        try {
            sha3_512 = MessageDigest.getInstance("SHA3-512");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Nonnull
    public static String toSHA(@Nonnull final String str) {
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        final byte[] result = sha3_512.digest(bytes);

        return String.format("%040x", new BigInteger(1, result));
    }

    private static final Pattern colorPattern = Pattern.compile("&([0-9a-fk-or])");

    public static String getColoredString(final String str) {
        final Matcher m = colorPattern.matcher(str);
        return m.find() ? m.replaceAll("§$1") : str;
    }

    private static final Pattern colorPattern2 = Pattern.compile("§([0-9a-fk-or])");

    @Nullable
    public static String getUncoloredString(final String str) {
        final Matcher m = colorPattern2.matcher(str);
        return m.find() ? m.replaceAll("&$1") : str;
    }

    public static int getLength(final String str) {
        return str == null ? -1 : str.length();
    }

    @Nullable
    public static <T> T getIndex(final T[] arr, final int index) {
        if (arr == null || arr.length <= index) return null;
        return arr[index];
    }

    @Nullable
    public static <T> T getIndex(final List<T> list, final int index) {
        if (list.size() <= index) return null;
        return list.get(index);
    }

    @Nonnull
    public static String join(final String character, final List<Object> list) {
        if (list == null) return "";
        return join(character, list.toArray());
    }

    @Nonnull
    public static String join(final String character, final Object[] list) {
        if (list == null) return "";
        return Arrays.stream(list).filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(character));
    }

    @Nullable
    public static Boolean getBoolean(final String bool) {
        if (bool == null) return null;
        if (bool.equalsIgnoreCase("true")) return true;
        if (bool.equalsIgnoreCase("false")) return false;
        return null;
    }

    @Nullable
    public static UUID toUuid(@Nullable final String str) {
        if (str == null) return null;
        return UUID.fromString(str);
    }
}
