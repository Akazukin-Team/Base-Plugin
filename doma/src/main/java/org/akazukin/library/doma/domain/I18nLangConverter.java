package org.akazukin.library.doma.domain;

import org.akazukin.i18n.manager.data.I18nLang;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class I18nLangConverter implements DomainConverter<I18nLang, String> {
    @Override
    public String fromDomainToValue(final I18nLang uuid) {
        return uuid.getId();
    }

    @Override
    public I18nLang fromValueToDomain(final String s) {
        return new I18nLang(s, s);
    }
}
