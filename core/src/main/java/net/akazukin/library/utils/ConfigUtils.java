package net.akazukin.library.utils;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        configFolder = plugin.getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
    }

    public void loadConfigFiles(final String... filenames) {
        for (final String filename : filenames) {
            final File configFile = new File(configFolder, filename);
            try {
                if (!configFile.exists()) {
                    if (filename.contains("/")) {
                        Files.createDirectories(configFolder.toPath().resolve(filename.substring(0, filename.lastIndexOf("/"))));
                    }
                    configFile.createNewFile();
                    try (final InputStream in = plugin.getResource("assets/net/akazukin/" + id + "/configs/" + filename)) {
                        try (final OutputStream out = Files.newOutputStream(configFile.toPath())) {
                            final byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                }
                final Configuration config = new Configuration(configFile);
                config.load();
                configs.put(filename, config);
            } catch (final NullPointerException e) {
                throw new RuntimeException("Not found default configuration!  " + filename);
            } catch (final IOException | InvalidConfigurationException e) {
                throw new RuntimeException("Configuration was broken!");
            }
        }
    }

    public void save(final String filename) {
        if (configs.containsKey(filename)) {
            try {
                configs.get(filename).save();
            } catch (final Exception e) {
                printException(e, filename);
            }
        }
    }

    public void reload(final String filename) {
        if (configs.containsKey(filename)) {
            try {
                configs.get(filename).load();
            } catch (final Exception e) {
                printException(e, filename);
            }
        }
    }

    public YamlConfiguration getConfig(final String filename) {
        if (configs.containsKey(filename))
            return configs.get(filename).getConfig();
        else
            return null;
    }

    private void printException(final Exception e, final String filename) {
        if (e instanceof IOException) {
            plugin.getLogger().severe("I/O exception while handling " + filename);
        } else if (e instanceof InvalidConfigurationException) {
            plugin.getLogger().severe("Invalid configuration in " + filename);
        }
        e.printStackTrace();
    }

    public List<String> getFiles() {
        final List<String> files = new ArrayList<>();
        for (final Entry<String, Configuration> config : configs.entrySet()) {
            files.add(config.getKey());
        }
        return files;
    }

    private static class Configuration {
        private final File configFile;
        @Getter
        private final YamlConfiguration config;

        public Configuration(final File configFile) {
            this.configFile = new File(configFile.getPath());
            config = new YamlConfiguration();
        }

        public void load() throws IOException, InvalidConfigurationException {
            config.load(configFile);
        }

        public void save() throws IOException {
            config.save(configFile);
        }
    }
}
