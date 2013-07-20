package rxinvoice.domain;

/**
 */
public class Address {
    private String body;
    private String zipCode;
    private String city;

    public String getBody() {
        return body;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public Address setBody(final String body) {
        this.body = body;
        return this;
    }

    public Address setZipCode(final String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public Address setCity(final String city) {
        this.city = city;
        return this;
    }

    @Override
    public String toString() {
        return "Address{" +
                "lines='" + body + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}

