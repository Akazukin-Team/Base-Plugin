package org.akazukin.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IOUtils {
    public static String toString(final InputStream inputStream) throws IOException {
        return new String(readAllBytes(inputStream), StandardCharsets.UTF_8);
    }

    public static byte[] readAllBytes(final InputStream is) throws IOException {
        try (final ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            final byte[] data = new byte[1024 * 8]; //8KB
            int read;
            while ((read = is.read(data)) != -1) {
                buf.write(data, 0, read);
            }
            return buf.toByteArray();
        }
    }
}
