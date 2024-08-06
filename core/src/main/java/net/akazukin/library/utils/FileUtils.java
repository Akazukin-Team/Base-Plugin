package net.akazukin.library.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.stream.Stream;
import lombok.NonNull;
import net.akazukin.library.LibraryPluginProvider;

public class FileUtils {
    public static String getApplicationPath() {
        try {
            return new File(".").getCanonicalPath() + (new File(".").getCanonicalPath().contains("/") ? "/" : "\\");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*public static String getInJarPath() {
        try {
            ProtectionDomain pd = FileUtils.class.getProtectionDomain();
            CodeSource cs = pd.getCodeSource();
            String path = cs.getLocation().getPath();
            return "file:" + URLDecoder.decode(path, "UTF-8") + "!/";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }*/

    public static InputStream getInJarFileStream(final String path) {
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }

    /*public static InputStream getFileStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException ignored) {
        }
        return null;
    }*/

    public static void createDirectory(final String path) {
        createDirectory(Paths.get(path));
    }

    public static void createDirectory(final Path path) {
        try {
            Files.createDirectory(path);
        } catch (final FileAlreadyExistsException ignored) {
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean delete(@NonNull final File file) {
        if (!file.exists()) return true;

        try {
            if (file.isFile()) {
                return file.delete();
            } else {
                return Files.walk(file.getAbsoluteFile().toPath())
                        .map(Stream::of)
                        .reduce(Stream.empty(), (x, y) -> Stream.concat(y, x))
                        .map(Path::toFile)
                        .peek(f -> {
                            if (!f.canWrite()) f.setWritable(true);
                        })
                        .allMatch(File::delete);
            }
        } catch (final Throwable ignored) {
            LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, "Failed to remove file", ignored);
            return false;
        }
    }
}
