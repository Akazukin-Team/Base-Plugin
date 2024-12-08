package org.akazukin.library.i18n;

import java.io.File;

public class I18nUtils extends org.akazukin.i18n.I18nUtils {
    public I18nUtils(final ClassLoader classLoader, String domain, String appId, final File dataFolder, final String defaultLocale) {
        super(classLoader, domain, appId, dataFolder, defaultLocale);
    }
}
