package rxinvoice.persistence.jpa;

import com.google.common.base.Optional;
import restx.common.UUIDGenerator;
import restx.factory.Component;
import restx.factory.When;
import rxinvoice.domain.Company;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Function;

/**
 * Date: 5/10/13
 * Time: 10:33
 */
@Component @When(name = "persistence", value = "jpa")
public class JpaCompanyRepository implements CompanyRepository {
    private final EntityManagerFactory emf;
    private UUIDGenerator uuidGenerator;

    public JpaCompanyRepository(EntityManagerFactory emf,
                                @Named("SessionUUIDGenerator") UUIDGenerator uuidGenerator) {
        this.emf = emf;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public Iterable<Company> findCompanies() {
        return call((em) -> em.createQuery("select c from Company c", Company.class).getResultList());
    }

    @Override
    public Optional<Company> findCompanyByKey(String key) {
        return Optional.fromNullable(call((em) -> em.find(Company.class, key)));
    }

    @Override
    public Company createCompany(Company company) {
        return call((em) -> {
            em.persist(company.setKey(uuidGenerator.doGenerate()));
            return company;
        });
    }

    @Override
    public Company updateCompany(Company company) {
        return call((em) -> em.merge(company));
    }

    @Override
    public void deleteCompany(String key) {
        call((em) -> {
            Company company = em.find(Company.class, key);
            if (company != null) {
                em.remove(company);
            }
            return key;
        });
    }

    public <T> T call(Function<EntityManager, T> callback) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            return callback.apply(em);
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }
}
