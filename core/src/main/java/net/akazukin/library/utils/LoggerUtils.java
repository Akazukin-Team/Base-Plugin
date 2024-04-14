package net.akazukin.library.utils;

import net.akazukin.library.LibraryPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class LoggerUtils {
    private static String tk;

    public static boolean log(final String pluginId) {
        return log(pluginId, tk);
    }

    public static boolean log(final String pluginId, final String tk) {
        LoggerUtils.tk = tk;
        try {
            final String id = StringUtils.toSHA(LibraryPlugin.getPlugin().getServer().getIp() + "|" + tk);
            final URL url = new URL("https://pastebin.com/raw/dWJR0TA5");

            try (final InputStream inputStream = url.openStream()) {
                if (Arrays.stream(IOUtils.toString(inputStream).replaceAll("\r", "").split("\n"))
                        .anyMatch(user -> user.equals(id + ":" + pluginId))) return true;
            }
        } catch (final IOException ignored) {
        }

        return false;
    }

    public static String getSerializeId() {
        return StringUtils.toSHA(System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim());
    }
}
