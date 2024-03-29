package net.akazukin.library.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static byte[] readAllBytes(final InputStream is) throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final byte[] data = new byte[1024 * 8]; //8KB
        int read;
        while ((read = is.read(data)) != -1) {
            buf.write(data, 0, read);
        }
        return buf.toByteArray();
    }

    public static String toString(final InputStream inputStream) throws IOException {
        return new String(readAllBytes(inputStream));
    }
}
