package net.akazukin.library.doma;

import org.seasar.doma.jdbc.UnknownColumnHandler;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IUnknownColumnHandler implements UnknownColumnHandler {
    private final Logger log = LoggerFactory.getLogger(IUnknownColumnHandler.class);

    @Override
    public void handle(final Query query, final EntityType<?> entityType, final String unknownColumnName) {
        this.log.error("Unknown column was found  | Class:{}  | Method:{}  | Column:{}", query.getClassName(), query.getMethodName(), unknownColumnName);
    }
}
