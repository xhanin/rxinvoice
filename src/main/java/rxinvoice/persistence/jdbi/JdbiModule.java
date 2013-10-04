package rxinvoice.persistence.jdbi;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import restx.factory.Module;
import restx.factory.Provides;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Date: 4/10/13
 * Time: 22:46
 */
@Module
public class JdbiModule {
    @Provides public DBI dbi(DataSource ds) {
        return init(new DBI(ds));
    }

    private DBI init(DBI dbi) {
        try (Handle h = dbi.open()) {
            h.execute("create table if not exists companies (" +
                    "key varchar2(36), " +
                    "name varchar2(40), " +
                    "addr_body varchar2(100), " +
                    "city varchar2(50), " +
                    "zipcode varchar2(10), " +
                    "primary key (key)" +
                    ")");
        }

        return dbi;
    }


}
