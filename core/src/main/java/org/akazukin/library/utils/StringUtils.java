package org.akazukin.library.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern UNCOLORED_PATTERN = Pattern.compile("&([0-9a-fk-or])");
    private static final Pattern COLORED_PATTERN = Pattern.compile("§([0-9a-fk-or])");

    @Nullable
    public static String getColoredString(@Nullable final CharSequence str) {
        if (org.akazukin.util.utils.StringUtils.isEmpty(str)) {
            return str != null ? str.toString() : null;
        }
        final Matcher m = UNCOLORED_PATTERN.matcher(str);
        return m.find() ? m.replaceAll("§$1") : str.toString();
    }

    @Nullable
    public static String getUncoloredString(@Nullable final CharSequence str) {
        if (org.akazukin.util.utils.StringUtils.isEmpty(str)) {
            return str != null ? str.toString() : null;
        }
        final Matcher m = COLORED_PATTERN.matcher(str);
        return m.find() ? m.replaceAll("&$1") : str.toString();
    }

    @NotNull
    public static String toStringOrEmpty(@Nullable final String obj) {
        return (obj == null) ? "" : obj;
    }

    public static String formatMS(final long sec) {
        return (sec / 60) + ":" + ((sec % 60) < 10 ? "0" : "") + (sec % 60);
    }

    public static boolean isNumeric(@Nullable final CharSequence str) {
        if (str == null) {
            return false;
        }
        //final char[] chars = str.toCharArray();
        final Character[] chars = str.chars().mapToObj(i -> (char) i).toArray(Character[]::new);
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (!Character.isDigit(c) &&
                    !(i == 0 && chars.length >= 2 && Objects.equals('-', c))) {
                return false;
            }
        }
        return true;
        //return str.chars().parallel().allMatch(Character::isDigit);
    }
}
