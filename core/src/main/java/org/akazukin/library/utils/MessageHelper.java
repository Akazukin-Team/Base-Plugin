package org.akazukin.library.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;
import org.akazukin.library.i18n.I18n;
import org.akazukin.library.i18n.I18nUtils;

public abstract class MessageHelper {
    private final List<I18nUtils> i18nUtils;

    public MessageHelper(final I18nUtils... i18nUtils) {
        this.i18nUtils = new ArrayList<>(Arrays.asList(i18nUtils));
        Collections.reverse(this.i18nUtils);
    }

    public void broadcast(final I18n message) {
        this.broadcast(this.get(this.getLocale(), message));
    }

    public void broadcast(final String message) {
        this.sendServer("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendServer(String msg);

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

    public abstract String getLocale();

    public void sendMessage(final ICmdSender sender, final I18n i18n, final Object... args) {
        if (sender instanceof IPlayerCmdSender) {
            this.sendMessage(((IPlayerCmdSender) sender).getUniqueId(), i18n, args);
        } else {
            this.consoleMessage(i18n, args);
        }
    }

    public void consoleMessage(final I18n message, final Object... args) {
        this.consoleMessage(this.get(this.getLocale(), message, args));
    }

    public void consoleMessage(final String message) {
        this.sendConsole("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendConsole(String msg);

    public void sendMessage(final UUID player, final I18n message, final Object... args) {
        this.sendMessage(player, this.get(this.getLocale(player), message, args));
    }

    public String getLocale(final UUID player) {
        final MUserEntity entity =
                LibrarySQLConfig.singleton().getTransactionManager().required(() -> MUserRepo.selectById(player));
        if (entity != null && entity.getLocale() != null) return entity.getLocale();
        return this.getLocale();
    }

    public void sendMessage(final UUID player, final String message) {
        if (player == null) return;
        this.sendPlayer(player, "§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendPlayer(UUID player, String msg);
}
