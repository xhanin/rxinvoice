package rxinvoice.persistence;

import com.google.common.base.Optional;
import rxinvoice.domain.Company;

/**
 * Created with IntelliJ IDEA.
 * User: xavierhanin
 * Date: 4/10/13
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
public interface CompanyRepository {
    Iterable<Company> findCompanies();

    Optional<Company> findCompanyByKey(String key);

    Company createCompany(Company company);

    Company updateCompany(Company company);

    void deleteCompany(String key);
}
