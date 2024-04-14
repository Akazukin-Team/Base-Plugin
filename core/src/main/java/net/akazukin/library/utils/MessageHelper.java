package net.akazukin.library.utils;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.doma.LibrarySQLConfig;
import net.akazukin.library.doma.entity.MUserEntity;
import net.akazukin.library.doma.repo.MUserRepo;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.i18n.I18nUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MessageHelper {
    private final List<I18nUtils> i18nUtils;

    public MessageHelper(final I18nUtils... i18nUtils) {
        this.i18nUtils = new ArrayList<>(Arrays.asList(i18nUtils));
        Collections.reverse(this.i18nUtils);
    }

    public static String getLocale(final UUID player) {
        final MUserEntity entity = LibrarySQLConfig.singleton().getTransactionManager().required(() -> MUserRepo.selectById(player));
        if (entity != null && entity.getLocale() != null) return entity.getLocale();
        return getLocale();
    }

    public static String getLocale() {
        return LibraryPlugin.CONFIG_UTILS.getConfig("config.yaml").getString("locale");
    }

    public String get(final String locale, final I18n i18n, final Object... args) {
        for (final I18nUtils i18nUtil : i18nUtils) {
            final String result = i18nUtil.get(locale, i18n, args);
            if (result != null) return result;
        }
        return null;
    }

    public void broadcast(final I18n message) {
        broadcast(get(getLocale(), message));
    }

    public void broadcast(final String message) {
        Bukkit.broadcastMessage(get(getLocale(), I18n.of("library.message.prefix")) + " " + message);
    }

    public void sendMessage(final CommandSender sender, final String message) {
        if (sender instanceof Player) {
            sendMessage((Player) sender, message);
        } else {
            consoleMessage(message);
        }
    }

    public void sendMessage(final CommandSender sender, final I18n message, final Object... args) {
        if (sender instanceof Player) {
            sendMessage((Player) sender, message, args);
        } else {
            consoleMessage(message, args);
        }
    }

    public void sendMessage(final HumanEntity player, final I18n message, final Object... args) {
        sendMessage(player, get(getLocale(player.getUniqueId()), message, args));
    }

    public void sendMessage(final HumanEntity player, final String message) {
        player.sendMessage("§7[§6§lAKZ§7]§e " + message);
    }

    public void sendMessage(final UUID player, final I18n message, final Object... args) {
        sendMessage(player, get(getLocale(player), message, args));
    }

    public void sendMessage(final UUID player, final String message) {
        sendMessage(Bukkit.getPlayer(player), message);
    }

    public void consoleMessage(final I18n message, final Object... args) {
        Bukkit.getConsoleSender().sendMessage("§7[§6§lAKZ§7]§e " + get(getLocale(), message, args));
    }

    public void consoleMessage(final String message) {
        Bukkit.getConsoleSender().sendMessage(get(getLocale(), I18n.of("library.message.prefix")) + " " + message);
    }
}
