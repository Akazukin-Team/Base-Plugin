package net.akazukin.library.doma.domain;

import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import java.util.UUID;

@ExternalDomain
public class UUIDConverter implements DomainConverter<UUID, String> {
    @Override
    public String fromDomainToValue(final UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID fromValueToDomain(final String s) {
        return UUID.fromString(s);
    }
}
