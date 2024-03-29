package net.akazukin.library.doma;

import lombok.Getter;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

import javax.sql.DataSource;

@Getter
public class SQLConfig implements Config {
    private static SQLConfig CONFIG;

    private final Dialect dialect;
    private final LocalTransactionDataSource localTransactionDataSource;
    private final TransactionManager transactionManager;
    private final DataSource dataSource;
    private final IJdbcLogger jdbcLogger;
    private final IUnknownColumnHandler unknownColumnHandler;

    public SQLConfig() {
        this.dialect = new SqliteDialect();
        this.dataSource = new LocalTransactionDataSource(
                "jdbc:sqlite://mapart.db",
                "",
                ""
        );
        this.localTransactionDataSource = new LocalTransactionDataSource(this.dataSource);
        this.transactionManager = new LocalTransactionManager(this.localTransactionDataSource.getLocalTransaction(getJdbcLogger()));
        this.jdbcLogger = new IJdbcLogger();
        this.unknownColumnHandler = new IUnknownColumnHandler();
    }

    public static SQLConfig singleton() {
        if (CONFIG == null) CONFIG = new SQLConfig();
        return CONFIG;
    }

    @Override
    public int getBatchSize() {
        return 1000;
    }
}
