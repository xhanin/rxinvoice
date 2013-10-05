package rxinvoice.persistence.j8ser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import jsonpersister.PersistentList;
import restx.common.UUIDGenerator;
import restx.factory.Component;
import restx.factory.When;
import rxinvoice.domain.Company;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.indexOf;

/**
 * Date: 4/10/13
 * Time: 23:53
 */
@Component @When(name = "persistence", value = "j8ser")
public class J8SerCompanyRepostory implements CompanyRepository {
    private final PersistentList<Company> companies;
    private final UUIDGenerator uuidGenerator;


    public J8SerCompanyRepostory(ObjectMapper objectMapper,
                                 @Named("SessionUUIDGenerator") UUIDGenerator uuidGenerator) {
        this.companies = PersistentList.on(Company.class, new File("db/companies.json"), objectMapper);
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public Iterable<Company> findCompanies() {
        return companies.list();
    }

    @Override
    public Optional<Company> findCompanyByKey(final String key) {
        return Optional.fromNullable(companies.list().stream().filter((c) -> c.getKey().equals(key)).findFirst().orElse(null));
    }

    @Override
    public Company createCompany(Company company) {
        companies.add(company.setKey(uuidGenerator.doGenerate()));
        return company;
    }

    @Override
    public Company updateCompany(Company company) {
        companies.set((c) -> c.getKey().equals(company.getKey()), company);
        return company;
    }

    @Override
    public void deleteCompany(String key) {
        companies.removeIf((c) -> c.getKey().equals(key));
    }
}
