package rxinvoice.rest;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.http.HttpStatus;
import restx.Status;
import restx.WebException;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.RolesAllowed;
import rxinvoice.AppModule;
import rxinvoice.domain.Company;
import rxinvoice.domain.User;

import javax.inject.Named;

import static restx.common.MorePreconditions.checkEquals;
import static rxinvoice.AppModule.Roles.ADMIN;
import static rxinvoice.AppModule.Roles.SELLER;

/**
 */
@Component @RestxResource
public class CompanyResource {
    private final JongoCollection companies;

    public CompanyResource(@Named("companies") JongoCollection companies) {
        this.companies = companies;
    }

    @RolesAllowed({ADMIN, SELLER})
    @GET("/companies")
    public Iterable<Company> findCompanies() {
        return companies.get().find().as(Company.class);
    }

    @GET("/companies/{key}")
    public Optional<Company> findCompanyByKey(String key) {
        // users can only get their own company except admin and sellers
        User user = AppModule.currentUser();
        if (!key.equals(user.getCompanyRef())
                && !user.getPrincipalRoles().contains(ADMIN)
                && !user.getPrincipalRoles().contains(SELLER)) {
            throw new WebException(HttpStatus.FORBIDDEN);
        }

        return Optional.fromNullable(companies.get().findOne(new ObjectId(key)).as(Company.class));
    }

    @RolesAllowed(ADMIN)
    @POST("/companies")
    public Company createCompany(Company company) {
        companies.get().save(company);
        return company;
    }

    @RolesAllowed(ADMIN)
    @PUT("/companies/{key}")
    public Company updateCompany(String key, Company company) {
        checkEquals("key", key, "company.key", company.getKey());
        companies.get().save(company);
        return company;
    }

    @RolesAllowed(ADMIN)
    @DELETE("/companies/{key}")
    public Status deleteCompany(String key) {
        // TODO check that company is not referenced by users

        companies.get().remove(new ObjectId(key));
        return Status.of("deleted");
    }

}
