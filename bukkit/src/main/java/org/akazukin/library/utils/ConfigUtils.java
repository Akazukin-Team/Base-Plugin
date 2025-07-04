package org.akazukin.library.utils;

import lombok.Getter;
import org.akazukin.util.utils.IOUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ConfigUtils {

    private final TreeMap<String, Configuration> configs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final File configFolder;
    private final Plugin plugin;
    private final String id;

    public ConfigUtils(final Plugin plugin, final String id) {
        this.plugin = plugin;
        this.id = id;
        this.configFolder = plugin.getDataFolder();
        if (!this.configFolder.exists()) {
            this.configFolder.mkdirs();
        }
    }

    public void loadConfigFiles(final String... filenames) {
        for (final String filename : filenames) {
            final File configFile = new File(this.configFolder, filename);
            try {
                if (!configFile.exists()) {
                    if (filename.contains("/")) {
                        Files.createDirectories(this.configFolder.toPath().resolve(filename.substring(0, filename.lastIndexOf("/"))));
                    }
                    configFile.createNewFile();
                    try (final InputStream is = this.plugin.getResource("assets/org/akazukin/" + this.id + "/configs/" + filename)) {
                        try (final OutputStream os = Files.newOutputStream(configFile.toPath())) {
                            os.write(IOUtils.readAllBytes(is));
                        }
                    }
                }
                final Configuration config = new Configuration(configFile);
                config.load();
                try (final InputStream is =
                             this.plugin.getResource("assets/org/akazukin/" + this.id + "/configs/" + filename)) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        final YamlConfiguration def = new YamlConfiguration();
                        def.load(isr);
                        config.getConfig().setDefaults(def);
                    }
                }
                this.configs.put(filename, config);
            } catch (final NullPointerException e) {
                throw new RuntimeException("Not found default configuration!  " + filename);
            } catch (final IOException | InvalidConfigurationException e) {
                throw new RuntimeException("Configuration was broken!");
            }
        }
    }

    public void save(final String filename) {
        if (this.configs.containsKey(filename)) {
            try {
                this.configs.get(filename).save();
            } catch (final Exception e) {
                this.printException(e, filename);
            }
        }
    }

    private void printException(final Exception e, final String filename) {
        if (e instanceof IOException) {
            this.plugin.getLogger().severe("I/O exception while handling " + filename);
        } else if (e instanceof InvalidConfigurationException) {
            this.plugin.getLogger().severe("Invalid configuration in " + filename);
        }
        e.printStackTrace();
    }

    public void reload(final String filename) {
        if (this.configs.containsKey(filename)) {
            try {
                this.configs.get(filename).load();
            } catch (final Exception e) {
                this.printException(e, filename);
            }
        }
    }

    public YamlConfiguration getConfig(final String filename) {
        if (this.configs.containsKey(filename)) {
            return this.configs.get(filename).getConfig();
        } else {
            return null;
        }
    }

    public String[] getFiles() {
        final List<String> files = new ArrayList<>();
        for (final Entry<String, Configuration> config : this.configs.entrySet()) {
            files.add(config.getKey());
        }
        return this.configs.keySet().toArray(new String[0]);
    }

    private static class Configuration {
        private final File configFile;
        @Getter
        private final YamlConfiguration config;

        public Configuration(final File configFile) {
            this.configFile = new File(configFile.getPath());
            this.config = new YamlConfiguration();
        }

        public void load() throws IOException, InvalidConfigurationException {
            if (this.configFile == null) {
                return;
            }
            this.config.load(this.configFile);
        }

        public void save() throws IOException {
            this.config.save(this.configFile);
        }
    }
}
