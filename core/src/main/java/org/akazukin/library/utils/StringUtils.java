package org.akazukin.library.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringUtils {
    private static final Pattern colorPattern = Pattern.compile("&([0-9a-fk-or])");
    private static final Pattern colorPattern2 = Pattern.compile("§([0-9a-fk-or])");

    public static String getColoredString(final CharSequence str) {
        final Matcher m = colorPattern.matcher(str);
        return m.find() ? m.replaceAll("§$1") : str.toString();
    }

    @NonNull
    public static String getUncoloredString(final CharSequence str) {
        final Matcher m = colorPattern2.matcher(str);
        return m.find() ? m.replaceAll("&$1") : str.toString();
    }

    @NotNull
    public static String toStringOrEmpty(@Nullable final String obj) {
        return (obj == null) ? "" : obj;
    }

    public static String formatMS(final long sec) {
        return (sec / 60) + ":" + ((sec % 60) < 10 ? "0" : "") + (sec % 60);
    }

    public static boolean isNumeric(final CharSequence str) {
        if (str == null) return false;
        //final char[] chars = str.toCharArray();
        final Character[] chars = str.chars().mapToObj(i -> (char) i).toArray(Character[]::new);
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (!Character.isDigit(c) &&
                    !(i == 0 && chars.length >= 2 && Objects.equals('-', c))) return false;
        }
        return true;
        //return str.chars().parallel().allMatch(Character::isDigit);
    }
}
