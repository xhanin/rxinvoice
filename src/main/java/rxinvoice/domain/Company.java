package rxinvoice.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 */
@Entity
public class Company {
    @Id @ObjectId @javax.persistence.Id
    private String key;

    private String name;

    @Embedded
    private Address address;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Company setKey(final String key) {
        this.key = key;
        return this;
    }

    public Company setName(final String name) {
        this.name = name;
        return this;
    }

    public Company setAddress(final Address address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "Company{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
