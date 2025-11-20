package org.akazukin.library.manager;

import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.i18n.object.I18nObject;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitMessageHelper extends MessageHelper {
    public BukkitMessageHelper(final II18nFormatter formatter) {
        super(formatter);
    }

    public static I18nLang getLocale(final HumanEntity player) {
        return LibraryPlugin.getPlugin().getMessageHelper().getLocale(player.getUniqueId());
    }

    public void sendMessage(final CommandSender sender, final I18nObject i18n) {
        if (sender instanceof Player) {
            this.sendMessage(((Player) sender).getUniqueId(), i18n);
        } else {
            this.consoleMessage(i18n);
        }
    }

    public void sendMessage(final HumanEntity sender, final I18nObject i18n) {
        this.sendMessage(sender.getUniqueId(), i18n);
    }

    @Override
    public I18nLang getLocale() {
        return LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getString("locale");
    }

    @Override
    protected void sendServer(final String msg) {
        Bukkit.broadcastMessage(msg);
    }

    @Override
    protected void sendConsole(final String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    protected void sendPlayer(final UUID player, final String msg) {
        Bukkit.getPlayer(player).sendMessage(msg);
    }
}
