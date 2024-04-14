package net.akazukin.library.doma;

import java.io.File;

public class LibrarySQLConfig extends SQLConfig {
    private static File FILE;
    private static LibrarySQLConfig CONFIG;

    public LibrarySQLConfig(final File database) {
        super(database);
    }

    public static void setFile(final File file) {
        FILE = file;
    }

    public static LibrarySQLConfig singleton() {
        if (CONFIG == null) CONFIG = new LibrarySQLConfig(FILE);
        return CONFIG;
    }
}
