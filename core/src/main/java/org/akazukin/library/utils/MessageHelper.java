package org.akazukin.library.utils;

import org.akazukin.i18n.I18n;
import org.akazukin.i18n.I18nObject;
import org.akazukin.i18n.I18nUtils;
import org.akazukin.i18n.I18nUtilsHolder;
import org.akazukin.library.command.ICmdSender;
import org.akazukin.library.command.IPlayerCmdSender;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class MessageHelper extends I18nUtilsHolder {
    public static final I18nUtils[] EMPTY_I18NUTILS = new I18nUtils[0];

    public MessageHelper(final I18nUtils... i18nUtils) {
        super(reverse(new ArrayList<>(Arrays.asList(i18nUtils))).toArray(EMPTY_I18NUTILS));
    }

    public static List<I18nUtils> reverse(final List<I18nUtils> i18nUtils) {
        Collections.reverse(i18nUtils);
        return i18nUtils;
    }

    public void broadcast(final I18n message) {
        this.broadcast(this.get(this.getLocale(), message));
    }

    public void broadcast(final String message) {
        this.sendServer("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendServer(String msg);

    public abstract String getLocale();

    @Override
    @Nullable
    public String get(final String locale, final I18nObject i18n) {
        return this.get(locale, i18n, true);
    }

    @Override
    public @Nullable String get(final String locale, final I18nObject i18n, final boolean defaultLocale) {
        return StringUtils.getColoredString(super.get(locale, i18n, defaultLocale));
    }

    public void sendMessage(final ICmdSender sender, final I18n i18n) {
        if (sender instanceof IPlayerCmdSender) {
            this.sendMessage(((IPlayerCmdSender) sender).getUniqueId(), i18n);
        } else {
            this.consoleMessage(i18n);
        }
    }

    public void consoleMessage(final I18n message) {
        this.consoleMessage(this.get(this.getLocale(), message));
    }

    public void consoleMessage(final String message) {
        this.sendConsole("§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendConsole(String msg);

    public void sendMessage(final UUID player, final I18n message) {
        this.sendMessage(player, this.get(this.getLocale(player), message));
    }

    public String getLocale(final UUID player) {
        final MUserEntity entity =
                LibrarySQLConfig.singleton().getTransactionManager().required(() -> MUserRepo.selectById(player));
        if (entity != null && entity.getLocale() != null) {
            return entity.getLocale();
        }
        return this.getLocale();
    }

    public void sendMessage(final UUID player, final String message) {
        if (player == null) {
            return;
        }
        this.sendPlayer(player, "§7[§6§lAKZ§7]§e " + message);
    }

    protected abstract void sendPlayer(UUID player, String msg);
}
