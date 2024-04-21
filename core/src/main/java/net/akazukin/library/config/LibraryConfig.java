package net.akazukin.library.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

public class LibraryConfig {
    public final static ConfigElement<List<String>> LOCALES = new ConfigElement<>("locales");
    public final static ConfigElement<String> LOCALE = new ConfigElement<>("locale");

    public static void load(final YamlConfiguration cfg) {
        if (cfg.get(LOCALES.getKey()) != null) LOCALES.setValue(cfg.get(LOCALES.getKey()));
        if (cfg.get(LOCALE.getKey()) != null) LOCALE.setValue(cfg.get(LOCALE.getKey()));
    }

    public static void save(final File file) throws IOException {
        final YamlConfiguration cfg = new YamlConfiguration();
        cfg.set(LOCALES.getKey(), LOCALES.getValue());
        cfg.set(LOCALE.getKey(), LOCALE.getValue());
        cfg.save(file);
    }
}
