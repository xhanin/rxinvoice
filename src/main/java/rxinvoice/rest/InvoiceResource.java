package rxinvoice.rest;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import rxinvoice.domain.Invoice;

import javax.inject.Named;

import static restx.common.MorePreconditions.checkEquals;

/**
 */
@Component @RestxResource
public class InvoiceResource {
    private final JongoCollection invoices;

    public InvoiceResource(@Named("invoices") JongoCollection invoices) {
        this.invoices = invoices;
    }

    @POST("/invoices")
    public Invoice createInvoice(Invoice invoice) {
        invoices.get().save(invoice);
        return invoice;
    }

    @PUT("/invoices/{key}")
    public Invoice updateInvoice(String key, Invoice invoice) {
        checkEquals("key", key, "invoice.key", invoice.getKey());
        invoices.get().save(invoice);
        return invoice;
    }

    @GET("/invoices")
    public Iterable<Invoice> findInvoices() {
        return invoices.get().find().as(Invoice.class);
    }

    @GET("/invoices/{key}")
    public Optional<Invoice> findInvoiceByKey(String key) {
        return Optional.fromNullable(invoices.get().findOne(new ObjectId(key)).as(Invoice.class));
    }


    @DELETE("/invoices/{key}")
    public Status deleteInvoice(String key) {
        invoices.get().remove(new ObjectId(key));

        return Status.of("deleted");
    }
}
