package org.akazukin.library.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.II18nManager;
import org.akazukin.i18n.manager.data.I18nLang;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nLocaleManager {
    II18nManager i18nManager;

    public I18nLocaleManager(final II18nManager i18nManager) {
        this.i18nManager = i18nManager;
    }

    // TODO implements the method.
    public I18nLang getByPlayer(final UUID player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public I18nLang getConsole() {
        return this.i18nManager.getFormatter().getFallbackLang();
    }

    // TODO implements the method.
    public void setByPlayer(final UUID player, final I18nLang i18nLang) {
    }
}
