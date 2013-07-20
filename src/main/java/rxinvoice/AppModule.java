package rxinvoice;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.joda.time.Duration;
import restx.SignatureKey;
import restx.factory.Module;
import restx.factory.Provides;
import restx.jongo.JongoFactory;
import restx.security.BasicPrincipalAuthenticator;
import restx.security.RestxPrincipal;
import restx.security.RestxSession;
import rxinvoice.domain.User;
import rxinvoice.rest.UserResource;

import javax.inject.Named;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey(
                 "rxinvoice -6496014073139514714 rxinvoice 2beab8fc-4422-46fc-8b80-4453071c3ff9"
                         .getBytes(Charsets.UTF_8));
    }

    @Provides @Named(JongoFactory.JONGO_DB_NAME)
    public String dbName() {
        return "rxinvoice";
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            final UserResource userResource) {
        return new BasicPrincipalAuthenticator() {
            @Override
            public Optional<? extends RestxPrincipal> findByName(String name) {
                return userResource.findUserByName(name);
            }

            @Override
            public Optional<? extends RestxPrincipal> authenticate(
                    String name, String passwordHash, ImmutableMap<String, ?> principalData) {
                boolean rememberMe = Boolean.valueOf((String) principalData.get("rememberMe"));

                Optional<User> u = userResource.findAndCheckCredentials(name, passwordHash);
                if (u.isPresent()) {
                    RestxSession.current().expires(rememberMe ? Duration.standardDays(30) : Duration.ZERO);
                }

                return u;
            }
        };
    }

}
