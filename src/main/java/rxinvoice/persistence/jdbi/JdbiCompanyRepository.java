package rxinvoice.persistence.jdbi;

import com.google.common.base.Optional;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import restx.common.UUIDGenerator;
import restx.factory.Component;
import restx.factory.When;
import rxinvoice.domain.Address;
import rxinvoice.domain.Company;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 */
@Component @When(name = "persistence", value = "jdbi")
public class JdbiCompanyRepository implements CompanyRepository {
    private final static ResultSetMapper<Company> COMPANY_RESULT_SET_MAPPER = new ResultSetMapper<Company>() {
        @Override
        public Company map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Company()
                    .setKey(r.getString("key"))
                    .setName(r.getString("name"))
                    .setAddress(new Address()
                            .setBody(r.getString("addr_body"))
                            .setZipCode(r.getString("zipCode"))
                            .setCity(r.getString("city"))
                    )
                    ;
        }
    };

    private final DBI dbi;
    private final UUIDGenerator uuidGenerator;

    public JdbiCompanyRepository(DBI dbi,
                                 @Named("SessionUUIDGenerator") UUIDGenerator uuidGenerator) {
        this.dbi = dbi;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public Iterable<Company> findCompanies() {
        try (Handle h = dbi.open()) {
            return h.createQuery("select * from companies")
                    .map(COMPANY_RESULT_SET_MAPPER)
                    .list();
        }
    }

    @Override
    public Optional<Company> findCompanyByKey(String key) {
        try (Handle h = dbi.open()) {
            return Optional.fromNullable(h.createQuery("select * from companies where key = :key")
                    .bind("key", key)
                    .map(COMPANY_RESULT_SET_MAPPER)
                    .first());
        }
    }

    @Override
    public Company createCompany(Company company) {
        company.setKey(uuidGenerator.doGenerate());

        try (Handle h = dbi.open()) {
            h.createStatement("insert into companies (key, name, addr_body, zipCode, city)" +
                    " values(:key, :name, :addrBody, :zipCode, :city)")
                    .bind("key", company.getKey())
                    .bind("name", company.getName())
                    .bind("addrBody", company.getAddress().getBody())
                    .bind("zipCode", company.getAddress().getZipCode())
                    .bind("city", company.getAddress().getCity())
                    .execute();
        }

        return company;
    }

    @Override
    public Company updateCompany(Company company) {
        try (Handle h = dbi.open()) {
            h.createStatement("update companies set name = :name, addr_body = :addrBody, zipCode = :zipCode, city = :city" +
                    " where key = :key")
                    .bind("key", company.getKey())
                    .bind("name", company.getName())
                    .bind("addrBody", company.getAddress().getBody())
                    .bind("zipCode", company.getAddress().getZipCode())
                    .bind("city", company.getAddress().getCity())
                    .execute();
        }
        return company;
    }

    @Override
    public void deleteCompany(String key) {
        try (Handle h = dbi.open()) {
            h.createStatement("delete from companies" +
                    " where key = :key")
                    .bind("key", key)
                    .execute();
        }
    }
}
