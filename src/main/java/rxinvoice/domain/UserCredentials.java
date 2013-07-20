package rxinvoice.domain;

import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 */
public class UserCredentials {
    @Id @ObjectId
    private String key;

    @ObjectId
    private String userRef;

    private String passwordHash;

    private DateTime lastUpdated;

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getKey() {
        return key;
    }

    public String getUserRef() {
        return userRef;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserCredentials setKey(final String key) {
        this.key = key;
        return this;
    }

    public UserCredentials setUserRef(final String userRef) {
        this.userRef = userRef;
        return this;
    }

    public UserCredentials setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserCredentials setLastUpdated(final DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "key='" + key + '\'' +
                ", userKey='" + userRef + '\'' +
                ", passwordHash='XXXXXXXXXXXX'" +
                '}';
    }
}
