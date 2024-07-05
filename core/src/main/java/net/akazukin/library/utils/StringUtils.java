package net.akazukin.library.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringUtils {
    private static final MessageDigest sha3_512;
    private static final Pattern colorPattern = Pattern.compile("&([0-9a-fk-or])");
    private static final Pattern colorPattern2 = Pattern.compile("§([0-9a-fk-or])");

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

    public static String getColoredString(final String str) {
        final Matcher m = colorPattern.matcher(str);
        return m.find() ? m.replaceAll("§$1") : str;
    }

    @Nullable
    public static String getUncoloredString(final String str) {
        final Matcher m = colorPattern2.matcher(str);
        return m.find() ? m.replaceAll("&$1") : str;
    }

    public static int getLength(final String str) {
        return str == null ? -1 : str.length();
    }

    @Nullable
    public static UUID toUuid(@Nullable final String str) {
        if (str == null) return null;
        return UUID.fromString(str);
    }

    public static String toStringOrEmpty(@Nullable final String obj) {
        return (obj == null) ? "" : obj;
    }
}
