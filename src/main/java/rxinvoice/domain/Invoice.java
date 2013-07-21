package rxinvoice.domain;

import org.joda.time.DateMidnight;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.jackson.FixedPrecision;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Invoice {
    @Id @ObjectId
    private String key;

    private String reference;
    private DateMidnight date;
    private Status status;

    private Company seller;
    private Company buyer;

    @FixedPrecision(2)
    private BigDecimal grossAmount;
    private List<VATVal> vats = new ArrayList<>();
    @FixedPrecision(2)
    private BigDecimal netAmount;

    private List<Line> lines = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public String getReference() {
        return reference;
    }

    public DateMidnight getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    public Company getSeller() {
        return seller;
    }

    public Company getBuyer() {
        return buyer;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public List<VATVal> getVats() {
        return vats;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public List<Line> getLines() {
        return lines;
    }

    public Invoice setKey(final String key) {
        this.key = key;
        return this;
    }

    public Invoice setReference(final String reference) {
        this.reference = reference;
        return this;
    }

    public Invoice setDate(final DateMidnight date) {
        this.date = date;
        return this;
    }

    public Invoice setStatus(final Status status) {
        this.status = status;
        return this;
    }

    public Invoice setSeller(final Company seller) {
        this.seller = seller;
        return this;
    }

    public Invoice setBuyer(final Company buyer) {
        this.buyer = buyer;
        return this;
    }

    public Invoice setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
        return this;
    }

    public Invoice setVats(final List<VATVal> vats) {
        this.vats = vats;
        return this;
    }

    public Invoice setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
        return this;
    }

    public Invoice setLines(final List<Line> lines) {
        this.lines = lines;
        return this;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "key='" + key + '\'' +
                ", reference='" + reference + '\'' +
                ", date=" + date +
                ", status=" + status +
                ", emitter=" + seller +
                ", recipient=" + buyer +
                ", grossAmount=" + grossAmount +
                ", vats=" + vats +
                ", netAmount=" + netAmount +
                ", lines=" + lines +
                '}';
    }

    public static enum Status {
        DRAFT, READY, SENT, LATE, PAID, CANCELLED
    }

    public static class VATVal {
        @FixedPrecision(2)
        private BigDecimal vat;

        @FixedPrecision(2)
        private BigDecimal amount;

        public BigDecimal getVat() {
            return vat;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public VATVal setVat(final BigDecimal vat) {
            this.vat = vat;
            return this;
        }

        public VATVal setAmount(final BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        @Override
        public String toString() {
            return "VATVal{" +
                    "vat=" + vat +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class Line {
        private String description;
        @FixedPrecision(2)
        private BigDecimal quantity;
        @FixedPrecision(2)
        private BigDecimal unitCost;
        @FixedPrecision(2)
        private BigDecimal grossAmount;

        private VATVal vat;

        public String getDescription() {
            return description;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitCost() {
            return unitCost;
        }

        public BigDecimal getGrossAmount() {
            return grossAmount;
        }

        public VATVal getVat() {
            return vat;
        }

        public Line setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Line setQuantity(final BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Line setUnitCost(final BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Line setGrossAmount(final BigDecimal grossAmount) {
            this.grossAmount = grossAmount;
            return this;
        }

        public Line setVat(final VATVal vat) {
            this.vat = vat;
            return this;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "description='" + description + '\'' +
                    ", quantity=" + quantity +
                    ", unitCost=" + unitCost +
                    ", grossAmount=" + grossAmount +
                    ", vat=" + vat +
                    '}';
        }
    }
}
