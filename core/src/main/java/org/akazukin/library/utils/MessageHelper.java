package org.akazukin.library.utils;

import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.I18n;
import org.akazukin.i18n.object.II18nObject;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;

import java.util.UUID;

public abstract class MessageHelper {
    II18nFormatter formatter;

    protected MessageHelper(final II18nFormatter formatter) {
        this.formatter = formatter;
    }

    public void broadcast(final II18nObject message) {
        this.broadcast(message.buildRequiredWithFallback(this.formatter, this.getLocale()));
    }

    public void broadcast(final String message) {
        this.sendServer("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendServer(String msg);

    public abstract II18nLang getLocale();

    public void consoleMessage(final II18nObject message) {
        this.consoleMessage(message.buildRequiredWithFallback(this.formatter, this.getLocale()));
    }

    public void consoleMessage(final String message) {
        this.sendConsole("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendConsole(String msg);

    public void sendMessage(final ICmdSender sender, final I18n i18n) {
        if (sender instanceof IPlayerCmdSender) {
            this.sendMessage(((IPlayerCmdSender) sender).getUniqueId(), i18n);
        } else {
            this.consoleMessage(i18n);
        }
    }

    public void sendMessage(final UUID player, final II18nObject message) {
        this.sendMessage(player,
                message.buildRequiredWithFallback(this.formatter, this.getLocale(player)));
    }

    public void sendMessage(final UUID player, final String message) {
        if (player == null) {
            return;
        }
        this.sendPlayer(player, "§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendPlayer(UUID player, String msg);

    public II18nLang getLocale(final UUID player) {
        final MUserEntity entity =
                LibrarySQLConfig.singleton().getTransactionManager().required(() -> MUserRepo.selectById(player));
        if (entity != null && entity.getLocale() != null) {
            return entity.getLocale();
        }
        return this.getLocale();
    }
}
