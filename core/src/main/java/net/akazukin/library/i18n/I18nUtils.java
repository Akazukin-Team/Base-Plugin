package net.akazukin.library.i18n;

import net.akazukin.library.LibraryPlugin;
import org.bukkit.plugin.Plugin;

import java.io.File;
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

public class I18nUtils {
    private static final Pattern otherI18 = Pattern.compile("<\\$[a-zA-Z0-9.]+>");
    private static final Pattern argsRegex = Pattern.compile("<args\\[[0-9]+]>");

    private static final Map<String, Properties> language = new HashMap<>();

    private final Plugin plugin;
    private final String pluginId;

    public I18nUtils(final Plugin plugin, final String pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;
    }

    public String get(final String id) {
        return get("en_us", id);
    }

    public String get(final String id, final I18n i18n) {
        return get("en_us", i18n.getKey());
    }

    public String get(final String id, final I18n i18n, final Object... args) {
        return get("en_us", i18n.getKey(), args);
    }

    public String get(final String locale, final String id, final Object... args) {
        if (!language.containsKey(locale)) throw new IllegalArgumentException("Invalid locale: " + locale);
        if (!language.get(locale).containsKey(id)) return null;
        String i18n = String.valueOf(language.get(locale).getProperty(id));

        i18n = i18n.replaceAll("\\\\n", "\n");

        Matcher m = argsRegex.matcher(i18n);

        for (int i = 0; i < args.length; i++) {
            if (!m.find()) break;

            if (args[i] == null) continue;

            final Pattern pattern = Pattern.compile(Pattern.quote("<args[" + i + "]>"));
            final Matcher m2 = pattern.matcher(i18n);
            if (m2.find()) {
                if (args[i] instanceof I18n) args[i] = get(locale, ((I18n) args[i]).getKey());
                i18n = m2.replaceAll(String.valueOf(args[i]));
            }

            m = argsRegex.matcher(i18n);
        }


        final Matcher m2 = otherI18.matcher(i18n);
        if (m2.find()) i18n = m2.replaceAll(get(locale, m2.group().substring(2, m2.group().length() - 1)));
        return i18n;
    }

    public void build(final String... locales) {
        final Properties props = new Properties();

        try (final InputStream is = plugin.getResource("assets/net/akazukin/" + pluginId + "/langs/en_us.lang")) {
            try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(isr);
            }
        } catch (final Exception e) {
            LibraryPlugin.getLogManager().log(Level.SEVERE, "Failed to load default localization file", e);
            throw new RuntimeException(e);
        }

        for (final String locale : locales) {
            final String langsFile = "langs/" + locale + ".lang";
            final Properties props2 = (Properties) props.clone();
            try (final InputStream is = new File(LibraryPlugin.getPlugin().getDataFolder(), langsFile).exists() ?
                    Files.newInputStream(new File(LibraryPlugin.getPlugin().getDataFolder(), langsFile).toPath()) :
                    plugin.getResource("assets/net/akazukin/" + pluginId + "/" + langsFile)) {
                try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    props2.load(isr);
                    language.put(locale, props2);
                }
            } catch (final Exception e) {
                LibraryPlugin.getLogManager().log(Level.SEVERE, "Failed to load localization file | " + langsFile, e);
            }
        }

        LibraryPlugin.getLogManager().info("Loaded " + language.size() + " languages");
    }
}
