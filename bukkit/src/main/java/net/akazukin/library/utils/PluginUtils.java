package net.akazukin.library.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginUtils {

    public static boolean isEnabled(final String pluginId) {
        final Plugin wg = Bukkit.getPluginManager().getPlugin(pluginId);
        return wg != null && wg.isEnabled();
    }
}
