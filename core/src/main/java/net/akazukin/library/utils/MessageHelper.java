package net.akazukin.library.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

public class MessageHelper {
    private final List<I18nUtils> i18nUtils;

    public MessageHelper(final I18nUtils... i18nUtils) {
        this.i18nUtils = new ArrayList<>(Arrays.asList(i18nUtils));
        Collections.reverse(this.i18nUtils);
    }

    public void broadcast(final I18n message) {
        this.broadcast(this.get(getLocale(), message));
    }

    public void broadcast(final String message) {
        Bukkit.broadcastMessage("§7[§6§lAKZ§7]§e " + message);
    }

    public String get(final String locale, final I18n i18n, final Object... args) {
        for (final I18nUtils i18nUtil : this.i18nUtils) {
            final String result = i18nUtil.get(locale, i18n, args);
            if (StringUtils.getLength(result) > 0) return StringUtils.getColoredString(result);
        }
        for (final I18nUtils i18nUtil : this.i18nUtils) {
            final String result = i18nUtil.get(i18n, args);
            if (StringUtils.getLength(result) > 0) return StringUtils.getColoredString(result);
        }
        return i18n.getKey();
    }

    public static String getLocale() {
        return LibraryPlugin.CONFIG_UTILS.getConfig("config.yaml").getString("locale");
    }

    public void sendMessage(final CommandSender sender, final String message) {
        if (sender instanceof Player) {
            this.sendMessage((Player) sender, message);
        } else {
            this.consoleMessage(message);
        }
    }

    public void sendMessage(final HumanEntity player, final String message) {
        if (player == null) return;
        player.sendMessage("§7[§6§lAKZ§7]§e " + message);
    }

    public void consoleMessage(final String message) {
        Bukkit.getConsoleSender().sendMessage("§7[§6§lAKZ§7]§e " + message);
    }

    public void sendMessage(final CommandSender sender, final I18n message, final Object... args) {
        if (sender instanceof Player) {
            this.sendMessage((Player) sender, message, args);
        } else {
            this.consoleMessage(message, args);
        }
    }

    public void sendMessage(final HumanEntity player, final I18n message, final Object... args) {
        this.sendMessage(player, this.get(getLocale(player.getUniqueId()), message, args));
    }

    public static String getLocale(final UUID player) {
        final MUserEntity entity =
                LibrarySQLConfig.singleton().getTransactionManager().required(() -> MUserRepo.selectById(player));
        if (entity != null && entity.getLocale() != null) return entity.getLocale();
        return getLocale();
    }

    public void consoleMessage(final I18n message, final Object... args) {
        this.consoleMessage(this.get(getLocale(), message, args));
    }

    public void sendMessage(final UUID player, final I18n message, final Object... args) {
        this.sendMessage(player, this.get(getLocale(player), message, args));
    }

    public void sendMessage(final UUID player, final String message) {
        this.sendMessage(Bukkit.getPlayer(player), message);
    }
}
