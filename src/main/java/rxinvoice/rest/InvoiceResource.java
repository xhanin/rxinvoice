package rxinvoice.rest;

import com.google.common.base.Optional;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.security.RolesAllowed;
import rxinvoice.domain.Invoice;
import rxinvoice.persistence.InvoiceRepository;
import rxinvoice.persistence.jongo.JongoInvoiceRepository;

import static restx.common.MorePreconditions.checkEquals;
import static rxinvoice.AppModule.Roles.ADMIN;
import static rxinvoice.AppModule.Roles.SELLER;

/**
 */
@Component @RestxResource
public class InvoiceResource {
    private final InvoiceRepository invoiceRepository;

    public InvoiceResource(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @RolesAllowed({ADMIN, SELLER})
    @POST("/invoices")
    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.createInvoice(invoice);
    }

    @RolesAllowed({ADMIN, SELLER})
    @PUT("/invoices/{key}")
    public Optional<Invoice> updateInvoice(String key, Invoice invoice) {
        checkEquals("key", key, "invoice.key", invoice.getKey());
        return invoiceRepository.updateInvoice(invoice);
    }

    @GET("/invoices")
    public Iterable<Invoice> findInvoices() {
        return invoiceRepository.findInvoices();
    }

    @GET("/invoices/{key}")
    public Optional<Invoice> findInvoiceByKey(String key) {
        return invoiceRepository.findInvoiceByKey(key);
    }

    @RolesAllowed({ADMIN, SELLER})
    @DELETE("/invoices/{key}")
    public Status deleteInvoice(String key) {
        invoiceRepository.deleteInvoice(key);
        return Status.of("deleted");
    }
}
