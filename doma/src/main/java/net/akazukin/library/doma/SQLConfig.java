package net.akazukin.library.doma;

import lombok.Getter;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcLogger;
import org.seasar.doma.jdbc.UnknownColumnHandler;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.slf4j.Slf4jJdbcLogger;

import java.io.File;

@Getter
public abstract class SQLConfig implements Config {
    private final Dialect dialect;
    private final LocalTransactionDataSource dataSource;
    private final JdbcLogger jdbcLogger;
    private final LocalTransactionManager transactionManager;
    private final UnknownColumnHandler unknownColumnHandler;

    public SQLConfig(final File database) {
        dialect = new SqliteDialect();
        dataSource = new LocalTransactionDataSource("jdbc:sqlite:" + database.getPath(), null, null);
        jdbcLogger = new Slf4jJdbcLogger();
        transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(jdbcLogger));
        unknownColumnHandler = new IUnknownColumnHandler();
    }

    @Override
    public int getBatchSize() {
        return 1000;
    }
}
