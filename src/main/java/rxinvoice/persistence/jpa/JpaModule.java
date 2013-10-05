package rxinvoice.persistence.jpa;

import org.hibernate.ejb.Ejb3Configuration;
import restx.factory.Module;
import restx.factory.Provides;
import rxinvoice.domain.Company;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Date: 5/10/13
 * Time: 10:39
 */
@Module
public class JpaModule {
    @Provides
    @SuppressWarnings("deprecation")
    public EntityManagerFactory emf(DataSource ds) {
        Properties properties = new Properties();
        properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        properties.put("hibernate.dialect" ,"org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto","update");
        properties.put("hibernate.show_sql","true");
        properties.put("hibernate.format_sql" ,"true");

        Ejb3Configuration cfg = new Ejb3Configuration();
        cfg.addProperties(properties);
        cfg.setDataSource(ds);
        cfg.addPackage("rxinvoice.domain");
        cfg.addAnnotatedClass(Company.class);
        return cfg.buildEntityManagerFactory();
    }
}
