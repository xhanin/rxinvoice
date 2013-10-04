package rxinvoice.persistence.jongo;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import rxinvoice.AppModule;
import rxinvoice.domain.Invoice;
import rxinvoice.domain.User;
import rxinvoice.persistence.CompanyRepository;
import rxinvoice.persistence.InvoiceRepository;

import javax.inject.Named;

import static com.google.common.base.Optional.absent;
import static rxinvoice.AppModule.Roles.ADMIN;
import static rxinvoice.AppModule.Roles.SELLER;

/**
 */
@Component
public class JongoInvoiceRepository implements InvoiceRepository {
    private static final Logger logger = LoggerFactory.getLogger(JongoInvoiceRepository.class);

    private final JongoCollection invoices;
    private final CompanyRepository companyRepository;

    public JongoInvoiceRepository(@Named("invoices") JongoCollection invoices, CompanyRepository companyRepository) {
        this.invoices = invoices;
        this.companyRepository = companyRepository;
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getSeller() == null) {
            User user = AppModule.currentUser();
            if (user.getPrincipalRoles().contains(SELLER)) {
                invoice.setSeller(companyRepository.findCompanyByKey(user.getCompanyRef()).get());
            }
        }
        invoices.get().save(invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> updateInvoice(Invoice invoice) {
        Optional<Invoice> invoiceByKey = findInvoiceByKey(invoice.getKey());
        if (!invoiceByKey.isPresent()) {
            return absent();
        }

        User user = AppModule.currentUser();
        if (!user.getPrincipalRoles().contains(ADMIN)) {
            Invoice invoiceFromDB = invoiceByKey.get();
            if (invoiceFromDB.getSeller() == null || invoiceFromDB.getSeller().getKey() == null
                    || !invoiceFromDB.getSeller().getKey().equals(user.getCompanyRef())) {
                logger.warn("a seller is trying to update an invoice from a different company: user: {} - invoice: {}",
                        user.getName(), invoice.getKey());
                return absent();
            }
        }

        invoices.get().save(invoice);
        return Optional.of(invoice);
    }

    @Override
    public Iterable<Invoice> findInvoices() {
        User user = AppModule.currentUser();
        if (user.getPrincipalRoles().contains(ADMIN)) {
            return invoices.get().find().as(Invoice.class);
        } else {
            return invoices.get().find("{ $or: [ { seller._id: #}, { buyer._id: #}]}",
                    new ObjectId(user.getCompanyRef()), new ObjectId(user.getCompanyRef())).as(Invoice.class);
        }
    }

    @Override
    public Optional<Invoice> findInvoiceByKey(String key) {
        Optional<Invoice> invoice = Optional.fromNullable(invoices.get().findOne(new ObjectId(key)).as(Invoice.class));
        if (invoice.isPresent()) {
            User user = AppModule.currentUser();
            if (!user.getPrincipalRoles().contains(ADMIN)) {
                if (((invoice.get().getSeller() == null || !user.getCompanyRef().equals(invoice.get().getSeller().getKey())))
                        && ((invoice.get().getBuyer() == null || !user.getCompanyRef().equals(invoice.get().getBuyer().getKey())))) {
                    return absent();
                }
            }
        }
        return invoice;
    }

    @Override
    public void deleteInvoice(String key) {
        Optional<Invoice> invoice = findInvoiceByKey(key);
        if (invoice.isPresent()) {
            invoices.get().remove(new ObjectId(key));
        }
    }
}
