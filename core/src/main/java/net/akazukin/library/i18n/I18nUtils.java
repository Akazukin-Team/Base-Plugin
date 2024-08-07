package net.akazukin.library.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.akazukin.library.LibraryPluginProvider;
import net.akazukin.library.utils.StringUtils;

@Getter
public class I18nUtils {
    private static final Pattern otherI18 = Pattern.compile("<\\$[a-zA-Z0-9.]+>");
    private static final Pattern argsRegex = Pattern.compile("<args\\[[0-9]+]>");

    private final Properties defaultLocale = new Properties();
    private final Map<String, Properties> language = new HashMap<>();

    private final Object plugin;
    private final String pluginId;
    private final File dataFolder;

    public I18nUtils(final Object plugin, final String pluginId, final File dataFolder) {
        this.plugin = plugin;
        this.pluginId = pluginId;
        this.dataFolder = dataFolder;
    }

    public String get(final I18n i18n, final Object... args) {
        return this.get(this.defaultLocale, i18n.getKey(), args);
    }

    public String get(final String locale, final I18n i18n, final Object... args) {
        return this.get(this.language.get(locale), i18n.getKey(), args);
    }

    public String get(final Properties locale, final String id, final Object... args) {
        //if ((!language.containsKey(locale) || !language.get(locale).containsKey(id)) && !defaultLocale.containsKey
        // (id))
        //    return id;
        if (locale == null || !locale.containsKey(id)) return null;

        String i18n = String.valueOf(locale.getProperty(id));
        if (StringUtils.getLength(i18n) <= 0) return null;

        i18n = i18n.replaceAll("\\\\n", "\n");

        Matcher m = argsRegex.matcher(i18n);

        for (int i = 0; i < args.length; i++) {
            if (!m.find()) break;

            final Pattern pattern = Pattern.compile(Pattern.quote("<args[" + i + "]>"));
            final Matcher m2 = pattern.matcher(i18n);
            if (m2.find()) {
                if (args[i] instanceof I18n) args[i] = this.get(locale, ((I18n) args[i]).getKey());
                i18n = m2.replaceAll(String.valueOf(args[i]));
            }

            m = argsRegex.matcher(i18n);
        }


        final Matcher m2 = otherI18.matcher(i18n);
        if (m2.find()) i18n = m2.replaceAll(this.get(locale, m2.group().substring(2, m2.group().length() - 1)));
        return (StringUtils.getLength(i18n) > 0) ? i18n : null;
    }

    public void build(final String... locales) {
        final String defaultDir = "/assets/net/akazukin/" + this.pluginId + "/";

        try (final InputStream is = this.plugin.getClass().getResourceAsStream(
                defaultDir + "langs/en_us.lang")) {
            if (is != null && is.available() > 0) {
                try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    this.defaultLocale.load(isr);
                }
            } else {
                final IllegalStateException e = new IllegalStateException("default locale was not found.");
                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, "Failed to load default localization" +
                        " for en_us", e);
                throw e;
            }
        } catch (final Exception e) {
            LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, "Failed to load default localization " +
                    "file", e);
            throw new RuntimeException(e);
        }

        for (final String locale : locales) {
            final String langsFile = "langs/" + locale + ".lang";
            final Properties props = new Properties();

            try (final InputStream is = this.plugin.getClass().getResourceAsStream(
                    defaultDir + langsFile)) {
                if (is != null && is.available() > 0) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } else {
                    LibraryPluginProvider.getApi().getLogManager().log(Level.WARNING, "Failed to load the default " +
                            "locale set for " + locale);
                }
            } catch (final IOException e) {
                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, "Failed to load localization file | "
                        + langsFile, e);
            }

            final File file = new File(this.dataFolder, langsFile);
            if (file.exists()) {
                try (final InputStream is = Files.newInputStream(file.toPath())) {
                    try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        props.load(isr);
                    }
                } catch (final IOException e) {
                    LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE,
                            "Failed to load custom localization file | " + langsFile, e);
                }
            }
            this.language.put(locale, props);
        }

        LibraryPluginProvider.getApi().getLogManager().info("Loaded " + this.language.size() + " languages");
    }
}
