package rxinvoice.persistence.j8ser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import restx.common.UUIDGenerator;
import restx.factory.Component;
import restx.factory.When;
import rxinvoice.domain.Company;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.collect.Iterables.indexOf;
import static com.google.common.collect.Iterables.tryFind;

/**
 * Date: 4/10/13
 * Time: 23:53
 */
@Component @When(name = "persistence", value = "j8ser")
public class J8SerCompanyRepostory implements CompanyRepository {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final File companiesFile = new File("db/companies.json");
    private static final List<Company> companies = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper;
    private final UUIDGenerator uuidGenerator;


    public J8SerCompanyRepostory(ObjectMapper objectMapper,
                                 @Named("SessionUUIDGenerator") UUIDGenerator uuidGenerator) {
        this.objectMapper = objectMapper;
        this.uuidGenerator = uuidGenerator;

        if (companies.isEmpty()) {
            read();
        }
    }

    @Override
    public Iterable<Company> findCompanies() {
        return companies;
    }

    @Override
    public Optional<Company> findCompanyByKey(final String key) {
        return Optional.fromNullable(companies.stream().filter((c) -> c.getKey().equals(key)).findFirst().orElse(null));
    }

    @Override
    public Company createCompany(Company company) {
        companies.add(company.setKey(uuidGenerator.doGenerate()));
        store();
        return company;
    }

    @Override
    public Company updateCompany(Company company) {
        int i = indexOf(companies, (c) -> c.getKey().equals(company.getKey()));
        if (i != -1) {
            companies.set(i, company);
            store();
        }
        return company;
    }

    @Override
    public void deleteCompany(String key) {
        companies.removeIf((c) -> c.getKey().equals(key));
        store();
    }

    private void store() {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    objectMapper.writeValue(companiesFile, companies);
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void read() {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    companies.clear();
                    companies.addAll(objectMapper.reader(new TypeReference<List<Company>>() { }).readValue(companiesFile));
                } finally {
                    lock.unlock();
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
