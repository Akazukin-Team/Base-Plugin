package net.akazukin.library.doma;

import java.io.File;
import lombok.Getter;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.UnknownColumnHandler;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;

@Getter
public abstract class SQLConfig implements Config {
    private final Dialect dialect;
    private final LocalTransactionDataSource dataSource;
    private final JdbcLogger jdbcLogger;
    private final LocalTransactionManager transactionManager;
    private final UnknownColumnHandler unknownColumnHandler;

    public SQLConfig(final File database) {
        dialect = new SqliteDialect();
        dataSource = new LocalTransactionDataSource("jdbc:sqlite:" + database.getPath() + "?jdbc.explicit_readonly=true&busy_timeout=1000000", null, null);
        jdbcLogger = new IJdbcLogger();
        transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));
        unknownColumnHandler = new IUnknownColumnHandler();
    }

    @Override
    public int getBatchSize() {
        return 1000;
    }
}
