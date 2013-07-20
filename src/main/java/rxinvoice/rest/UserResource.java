package rxinvoice.rest;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;
import restx.HttpStatus;
import restx.Status;
import restx.WebException;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.RestxSession;
import rxinvoice.domain.User;
import rxinvoice.domain.UserCredentials;

import javax.inject.Named;
import java.util.Arrays;
import java.util.Map;

/**
 */
@Component @RestxResource
public class UserResource {
    private final JongoCollection users;
    private final JongoCollection usersCredentials;
    private final String adminPasswordHash;
    private User defaultAdminUser = new User()
            .setKey(new ObjectId().toString())
            .setName("admin")
            .setRoles(Arrays.asList("admin", "restx-admin"));

    public UserResource(@Named("users") JongoCollection users,
                        @Named("usersCredentials") JongoCollection usersCredentials,
                        @Named("restx.admin.passwordHash") final String adminPasswordHash) {
        this.users = users;
        this.usersCredentials = usersCredentials;
        this.adminPasswordHash = adminPasswordHash;
    }

    @POST("/users")
    public User createUser(User user) {
        users.get().save(user);
        return user;
    }

    @PUT("/users/{key}")
    public User updateUser(String key, User user) {
        users.get().save(user);
        return user;
    }

    @GET("/users")
    public Iterable<User> findUsers() {
        return users.get().find().as(User.class);
    }

    @GET("/users/{key}")
    public Optional<User> findUserByKey(String key) {
        return Optional.fromNullable(users.get().findOne(new ObjectId(key)).as(User.class));
    }

    public Optional<User> findUserByName(String name) {
        Optional<User> user = Optional.fromNullable(users.get().findOne("{name: #}", name).as(User.class));
        if (!user.isPresent()
                && "admin".equals(name)
                && !isAdminDefined()) {
            // use in memory admin user as long as no user with admin role is defined in DB
            return Optional.of(defaultAdminUser);
        }
        return user;
    }

    @DELETE("/users/{key}")
    public Status deleteUser(String key) {
        ObjectId id = new ObjectId(key);
        users.get().remove(id);
        usersCredentials.get().remove("{ userRef: # }", id);
        return Status.of("deleted");
    }

    @PUT("/users/{userKey}/credentials")
    public Status setCredentials(String userKey, Map newCredentials) {
        String passwordHash = (String) newCredentials.get("passwordHash");
        Preconditions.checkNotNull(passwordHash, "new credentials must have a passwordHash property");

        User user = (User) RestxSession.current().getPrincipal().get();
        if (!user.getPrincipalRoles().contains("admin")
                && !user.getKey().equals(userKey)) {
            throw new WebException(HttpStatus.FORBIDDEN);
        }

        UserCredentials userCredentials = findCredentialsForUserKey(userKey);

        if (userCredentials == null) {
            userCredentials = new UserCredentials().setUserRef(userKey);
        }
        String hashed = BCrypt.hashpw(passwordHash, BCrypt.gensalt());
        usersCredentials.get().save(
                userCredentials
                        .setPasswordHash(hashed)
                        .setLastUpdated(DateTime.now()));

        return Status.of("updated");
    }

    public Optional<User> findAndCheckCredentials(String name, String passwordHash) {
        Optional<User> user = findUserByName(name);
        if (!user.isPresent()) {
            return Optional.absent();
        }

        UserCredentials credentials = findCredentialsForUserKey(user.get().getKey());

        if (credentials == null) {
            if ("admin".equals(name)) {
                // allow admin log in with config password as long as it is not defined in DB
                if (adminPasswordHash.equals(passwordHash)) {
                    return user;
                }
            }

            return Optional.absent();
        }

        if (BCrypt.checkpw(passwordHash, credentials.getPasswordHash())) {
            return user;
        } else {
            return Optional.absent();
        }
    }

    private UserCredentials findCredentialsForUserKey(String userKey) {
        return usersCredentials.get()
                .findOne("{ userRef: # }", new ObjectId(userKey)).as(UserCredentials.class);
    }

    private boolean isAdminDefined() {
        return users.get().count("{roles: {$all: [ # ]}}", "admin") > 0;
    }
}
