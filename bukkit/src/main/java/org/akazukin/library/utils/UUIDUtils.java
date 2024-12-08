package org.akazukin.library.utils;

import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

public class UUIDUtils {
    private final static Pattern UUID = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-[1,5][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);

    private final static Pattern UUID_V1 = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-1[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern UUID_V2 = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-2[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern UUID_V3 = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-3[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern UUID_V4 = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);
    private final static Pattern UUID_V5 = Pattern.compile(
            "^[0-9A-F]{8}-[0-9A-F]{4}-5[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);

    public static boolean isUUID(String uuid) {
        return UUID.matcher(uuid).matches();
    }

    public static boolean isUUID(String uuid, int version) {
        if (version == 1) {
            return UUID_V1.matcher(uuid).matches();
        } else if (version == 2) {
            return UUID_V2.matcher(uuid).matches();
        } else if (version == 3) {
            return UUID_V3.matcher(uuid).matches();
        } else if (version == 4) {
            return UUID_V4.matcher(uuid).matches();
        } else if (version == 5) {
            return UUID_V5.matcher(uuid).matches();
        } else {
            throw new UnsupportedOperationException("The UUID version is not suppored");
        }
    }

    @Nullable
    public static java.util.UUID toUuid(@Nullable final String str) {
        if (str == null) return null;
        return java.util.UUID.fromString(str);
    }
}
