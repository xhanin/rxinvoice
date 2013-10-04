package rxinvoice.persistence.jongo;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;
import restx.HttpStatus;
import restx.WebException;
import restx.exceptions.RestxError;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import rxinvoice.AppModule;
import rxinvoice.domain.User;
import rxinvoice.domain.UserCredentials;
import rxinvoice.persistence.CompanyRepository;
import rxinvoice.persistence.UserRepository;

import javax.inject.Named;
import java.util.Arrays;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static rxinvoice.AppModule.Roles.*;

/**
 */
@Component
public class JongoUserRepository implements UserRepository {
    private final JongoCollection users;
    private final JongoCollection usersCredentials;
    private final String adminPasswordHash;
    private final CompanyRepository companyRepository;

    private final User defaultAdminUser = new User()
            .setKey(new ObjectId().toString())
            .setName("admin")
            .setRoles(Arrays.asList(ADMIN, "restx-admin"));

    public JongoUserRepository(@Named("users") JongoCollection users,
                               @Named("usersCredentials") JongoCollection usersCredentials,
                               @Named("restx.admin.passwordHash") final String adminPasswordHash,
                               CompanyRepository companyRepository) {
        this.users = users;
        this.usersCredentials = usersCredentials;
        this.adminPasswordHash = adminPasswordHash;
        this.companyRepository = companyRepository;
    }

    @Override
    public User createUser(User user) {
        checkUserRules(user);
        users.get().save(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkSelfOrAdmin(user.getKey());
        checkUserRules(user);
        users.get().save(user);
        return user;
    }

    @Override
    public Iterable<User> findUsers() {
        return users.get().find().as(User.class);
    }

    @Override
    public Optional<User> findUserByKey(String key) {
        checkSelfOrAdmin(key);
        return Optional.fromNullable(users.get().findOne(new ObjectId(key)).as(User.class));
    }

    @Override
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

    @Override
    public void deleteUser(String key) {
        ObjectId id = new ObjectId(key);
        users.get().remove(id);
        usersCredentials.get().remove("{ userRef: # }", id);
    }

    @Override
    public void setCredentials(String userKey, String passwordHash) {
        checkSelfOrAdmin(userKey);

        UserCredentials userCredentials = findCredentialsForUserKey(userKey);

        if (userCredentials == null) {
            userCredentials = new UserCredentials().setUserRef(userKey);
        }
        String hashed = BCrypt.hashpw(passwordHash, BCrypt.gensalt());
        usersCredentials.get().save(
                userCredentials
                        .setPasswordHash(hashed)
                        .setLastUpdated(DateTime.now()));
    }

    private void checkUserRules(User user) {
        if (user.getPrincipalRoles().contains(SELLER) || user.getPrincipalRoles().contains(BUYER)) {
            if (user.getCompanyRef() == null) {
                throw RestxError.on(User.Rules.CompanyRef.class)
                        .set(User.Rules.CompanyRef.KEY, user.getKey())
                        .raise();
            }
            if (!companyRepository.findCompanyByKey(user.getCompanyRef()).isPresent()) {
                throw RestxError.on(User.Rules.ValidCompanyRef.class)
                        .set(User.Rules.ValidCompanyRef.KEY, user.getKey())
                        .set(User.Rules.ValidCompanyRef.COMPANY_REF, user.getCompanyRef())
                        .raise();
            }
        }
    }

    public static void checkSelfOrAdmin(String userKey) {
        User user = AppModule.currentUser();
        if (!user.getPrincipalRoles().contains(ADMIN)
                && !user.getKey().equals(userKey)) {
            throw new WebException(HttpStatus.FORBIDDEN);
        }
    }

    @Override
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
        return users.get().count("{roles: {$all: [ # ]}}", ADMIN) > 0;
    }
}
