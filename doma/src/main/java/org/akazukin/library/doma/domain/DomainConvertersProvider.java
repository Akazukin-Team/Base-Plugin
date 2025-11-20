package org.akazukin.library.doma.domain;

import org.seasar.doma.DomainConverters;

@DomainConverters({UUIDConverter.class, I18nLangConverter.class})
public class DomainConvertersProvider {
}
