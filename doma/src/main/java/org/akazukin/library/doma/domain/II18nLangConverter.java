package org.akazukin.library.doma.domain;

import org.akazukin.i18n.manager.data.I18nLang;
import org.akazukin.i18n.manager.data.II18nLang;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

@ExternalDomain
public class II18nLangConverter implements DomainConverter<II18nLang, String> {
    @Override
    public String fromDomainToValue(final II18nLang uuid) {
        return uuid.getId();
    }

    @Override
    public II18nLang fromValueToDomain(final String s) {
        return new I18nLang(s);
    }
}
