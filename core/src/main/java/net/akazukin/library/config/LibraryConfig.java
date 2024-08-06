package net.akazukin.library.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LibraryConfig {
    public final static ConfigElement<String[]> LOCALES = new ConfigElement<>("locales");
    public final static ConfigElement<String> LOCALE = new ConfigElement<>("locale");

    public static void load(final Properties cfg) {
        final List<String> locales = new ArrayList<>();
        for (int i = 0; cfg.containsKey(LOCALES.getKey() + ".[" + i + "]"); i++) {
            locales.add(String.valueOf(cfg.get(LOCALES.getKey() + ".[" + i + "]")));
        }
        if (cfg.get(LOCALES.getKey()) != null) LOCALES.setValue(locales.toArray(new String[0]));

        if (cfg.get(LOCALE.getKey()) != null) LOCALE.setValue(String.valueOf(cfg.get(LOCALE.getKey())));
    }

    public static void save(final File file) throws IOException {
        final Properties cfg = new Properties();
        for (int i = 0; i < LOCALES.getValue().length; i++) {
            cfg.put(LOCALES.getKey() + ".[" + i + "]", LOCALES.getValue()[i]);
        }
        cfg.put(LOCALE.getKey(), LOCALE.getValue());

        try (final FileOutputStream fos = new FileOutputStream(file)) {
            try (final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                osw.write(cfg.toString());
            }
        }
    }
}
