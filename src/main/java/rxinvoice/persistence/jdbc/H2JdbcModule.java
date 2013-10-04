package rxinvoice.persistence.jdbc;

import org.h2.jdbcx.JdbcConnectionPool;
import restx.factory.Module;
import restx.factory.Provides;

import javax.inject.Named;
import javax.sql.DataSource;

/**
 */
@Module
public class H2JdbcModule {
    @Provides @Named("restx.jdbc.url") public String jdbcUrl() {
        return "jdbc:h2:./db/rxinvoice";
    }
    @Provides @Named("restx.jdbc.username") public String jdbcUsername() {
        return "sa";
    }
    @Provides @Named("restx.jdbc.password") public String jdbcPassword() {
        return "";
    }

    @Provides
    public DataSource datasource(
            @Named("restx.jdbc.url") String jdbcUrl,
            @Named("restx.jdbc.username") String jdbcUsername,
            @Named("restx.jdbc.password") String jdbcPassword) {
        return JdbcConnectionPool.create(jdbcUrl, jdbcUsername, jdbcPassword);
    }
}
