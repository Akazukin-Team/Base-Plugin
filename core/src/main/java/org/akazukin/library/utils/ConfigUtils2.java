package org.akazukin.library.utils;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.library.LibraryPluginProvider;
import org.akazukin.util.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;

public class ConfigUtils2 {
    private final TreeMap<String, Configuration> configs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final File file;
    private final String id;
    private final Object plugin;

    public ConfigUtils2(final Object plugin, final File file, final String id) {
        this.plugin = plugin;
        this.file = file;
        this.id = id;
        if (!this.file.exists()) {
            this.file.mkdirs();
        }
    }

    public void loadConfigFiles(final String... filenames) {
        for (final String filename : filenames) {
            final File configFile = new File(this.file, filename);
            try {
                if (!configFile.exists()) {
                    if (filename.contains("/")) {
                        Files.createDirectories(this.file.toPath().resolve(filename.substring(0,
                                filename.lastIndexOf("/"))));
                    }
                    configFile.createNewFile();
                    try (final InputStream is = this.plugin.getClass().getResourceAsStream(
                            "assets/net/akazukin/" + this.id + "/configs/" + filename)) {
                        try (final OutputStream os = Files.newOutputStream(configFile.toPath())) {
                            os.write(IOUtils.readAllBytes(is));
                        }
                    }
                }
                final Configuration config = new Configuration(configFile);
                config.load();
                this.configs.put(filename, config);
            } catch (final NullPointerException e) {
                throw new RuntimeException("Not found default configuration!  " + filename);
            } catch (final IOException e) {
                throw new RuntimeException("Configuration was broken!");
            }
        }
    }

    public void save(final String filename) {
        if (this.configs.containsKey(filename)) {
            try {
                this.configs.get(filename).save();
            } catch (final Exception e) {
                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, filename, e);
            }
        }
    }

    public void reload(final String filename) {
        if (this.configs.containsKey(filename)) {
            try {
                this.configs.get(filename).load();
            } catch (final Exception e) {
            }
        }
    }

    public Properties getConfig(final String filename) {
        if (this.configs.containsKey(filename)) {
            return this.configs.get(filename).getConfig();
        } else {
            return null;
        }
    }

    public List<String> getFiles() {
        final List<String> files = new ArrayList<>();
        for (final Entry<String, Configuration> config : this.configs.entrySet()) {
            files.add(config.getKey());
        }
        return files;
    }

    private static class Configuration {
        private final File configFile;
        @Getter
        private final Properties config;
        @Getter
        @Setter
        private Properties defoultConfig;

        public Configuration(final File configFile) {
            this.configFile = new File(configFile.getPath());
            this.config = new Properties();
        }

        public void load() throws IOException {
            if (this.configFile == null) {
                return;
            }
            if (!this.configFile.exists()) {
                this.configFile.createNewFile();
            }

            this.config.clear();
            try (final InputStream is = Files.newInputStream(this.configFile.toPath())) {
                this.config.load(is);
            }
        }

        public void save() throws IOException {
            try (final FileOutputStream fos = new FileOutputStream(this.configFile)) {
                try (final OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                    osw.write(this.config.toString());
                }
            }
        }

        public Properties get() {
            if (this.config == null) {
                throw new IllegalStateException(this.configFile.getName() + " hasnt loaded yet");
            }
            return this.config;
        }
    }
}
