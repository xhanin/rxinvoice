package rxinvoice.persistence.jongo;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.HttpStatus;
import restx.Status;
import restx.WebException;
import restx.annotations.*;
import restx.factory.Alternative;
import restx.factory.Component;
import restx.factory.When;
import restx.jongo.JongoCollection;
import restx.security.RolesAllowed;
import rxinvoice.AppModule;
import rxinvoice.domain.Company;
import rxinvoice.domain.User;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;

import static restx.common.MorePreconditions.checkEquals;
import static rxinvoice.AppModule.Roles.ADMIN;
import static rxinvoice.AppModule.Roles.SELLER;

/**
 */
@Component
public class JongoCompanyRepository implements CompanyRepository {
    private final JongoCollection companies;

    public JongoCompanyRepository(@Named("companies") JongoCollection companies) {
        this.companies = companies;
    }

    @Override
    public Iterable<Company> findCompanies() {
        return companies.get().find().as(Company.class);
    }

    @Override
    public Optional<Company> findCompanyByKey(String key) {
        return Optional.fromNullable(companies.get().findOne(new ObjectId(key)).as(Company.class));
    }

    @Override
    public Company createCompany(Company company) {
        companies.get().save(company);
        return company;
    }

    @Override
    public Company updateCompany(Company company) {
        companies.get().save(company);
        return company;
    }

    @Override
    public void deleteCompany(String key) {
        companies.get().remove(new ObjectId(key));
    }
}
