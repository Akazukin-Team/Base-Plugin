package org.akazukin.library.i18n;

import lombok.Getter;

@Getter
public class I18n {
    private final String key;

    public I18n(final String key) {
        this.key = key;
    }

    public static I18n of(final String key) {
        return new I18n(key);
    }
}
