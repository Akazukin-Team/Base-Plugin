package org.akazukin.library.manager;

import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.II18nObject;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;
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

    public static II18nLang getLocale(final HumanEntity player) {
        return LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
            final MUserEntity entity = MUserRepo.selectById(player.getUniqueId());
            return entity == null ? null : entity.getLocale();
        });
    }

    public void sendMessage(final CommandSender sender, final II18nObject i18n) {
        if (sender instanceof Player) {
            this.sendMessage(((Player) sender).getUniqueId(), i18n);
        } else {
            this.consoleMessage(i18n);
        }
    }

    public void sendMessage(final HumanEntity sender, final II18nObject i18n) {
        this.sendMessage(sender.getUniqueId(), i18n);
    }

    @Override
    protected void sendServer(final String msg) {
        Bukkit.broadcastMessage(msg);
    }

    @Override
    public II18nLang getLocale() {
        final String locale = LibraryPlugin.getPlugin().getConfigUtils().getConfig("config.yaml").getString("locale");
        return new org.akazukin.i18n.manager.data.I18nLang(locale, locale);
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


