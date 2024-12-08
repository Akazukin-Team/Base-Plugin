package org.akazukin.library.manager;

import java.util.UUID;
import org.akazukin.i18n.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.i18n.I18nUtils;
import org.akazukin.library.utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class BukkitMessageHelper extends MessageHelper {
    public BukkitMessageHelper(final I18nUtils... i18nUtils) {
        super(i18nUtils);
    }

    public static String getLocale(final HumanEntity player) {
        return LibraryPlugin.getPlugin().getMessageHelper().getLocale(player.getUniqueId());
    }

    public void sendMessage(final CommandSender sender, final I18n i18n) {
        if (sender instanceof Player) {
            this.sendMessage(((Player) sender).getUniqueId(), i18n);
        } else {
            this.consoleMessage(i18n);
        }
    }

    public void sendMessage(final HumanEntity sender, final I18n i18n) {
        this.sendMessage(sender.getUniqueId(), i18n);
    }

    @Override
    protected void sendServer(final String msg) {
        Bukkit.broadcastMessage(msg);
    }

    @Override
    public String getLocale() {
        return LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getString("locale");
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
