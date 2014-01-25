package rxinvoice;

import com.google.common.base.Charsets;
import restx.mongo.MongoModule;
import restx.security.*;
import restx.factory.Module;
import restx.factory.Provides;
import rxinvoice.domain.User;
import rxinvoice.rest.AppUserRepository;
import rxinvoice.rest.UserResource;

import javax.inject.Named;

@Module
public class AppModule {
    public static User currentUser() {
        return (User) RestxSession.current().getPrincipal().get();
    }

    public static final class Roles {
        // we don't use an enum here because roles in @RolesAllowed have to be constant strings
        public static final String ADMIN = "admin";
        public static final String SELLER = "seller";
        public static final String BUYER = "buyer";
    }

    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey(
                 "rxinvoice -6496014073139514714 rxinvoice 2beab8fc-4422-46fc-8b80-4453071c3ff9"
                         .getBytes(Charsets.UTF_8));
    }

    @Provides @Named(MongoModule.MONGO_DB_NAME)
    public String dbName() {
        return "rxinvoice";
    }

    @Provides @Named("restx.app.package")
    public String appPackage() {
        return "rxinvoice";
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            AppUserRepository userRepository, SecuritySettings securitySettings,
            CredentialsStrategy credentialsStrategy,
            @Named("restx.admin.passwordHash") String adminPasswordHash) {
        return new StdBasicPrincipalAuthenticator(
                new StdUserService<>(userRepository, credentialsStrategy, adminPasswordHash), securitySettings);
    }
}
