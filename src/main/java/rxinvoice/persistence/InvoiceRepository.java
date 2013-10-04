package rxinvoice.persistence;

import com.google.common.base.Optional;
import rxinvoice.domain.Invoice;

/**
 * Created with IntelliJ IDEA.
 * User: xavierhanin
 * Date: 4/10/13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public interface InvoiceRepository {
    Invoice createInvoice(Invoice invoice);

    Optional<Invoice> updateInvoice(Invoice invoice);

    Iterable<Invoice> findInvoices();

    Optional<Invoice> findInvoiceByKey(String key);

    void deleteInvoice(String key);
}
