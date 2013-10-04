package rxinvoice.rest;

import com.google.common.base.Optional;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.security.RolesAllowed;
import rxinvoice.domain.User;
import rxinvoice.persistence.UserRepository;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static restx.common.MorePreconditions.checkEquals;
import static rxinvoice.AppModule.Roles.ADMIN;

/**
 */
@Component @RestxResource
public class UserResource {
    private final UserRepository userRepository;

    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RolesAllowed(ADMIN)
    @POST("/users")
    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    @PUT("/users/{key}")
    public User updateUser(String key, User user) {
        checkEquals("key", key, "user.key", user.getKey());
        return userRepository.updateUser(user);
    }

    @RolesAllowed(ADMIN)
    @GET("/users")
    public Iterable<User> findUsers() {
        return userRepository.findUsers();
    }

    @GET("/users/{key}")
    public Optional<User> findUserByKey(String key) {
        return userRepository.findUserByKey(key);
    }

    @RolesAllowed(ADMIN)
    @DELETE("/users/{key}")
    public Status deleteUser(String key) {
        userRepository.deleteUser(key);
        return Status.of("deleted");
    }

    @PUT("/users/{userKey}/credentials")
    public Status setCredentials(String userKey, Map newCredentials) {
        String passwordHash = (String) newCredentials.get("passwordHash");
        checkNotNull(passwordHash, "new credentials must have a passwordHash property");

        userRepository.setCredentials(userKey, passwordHash);

        return Status.of("updated");
    }
}
