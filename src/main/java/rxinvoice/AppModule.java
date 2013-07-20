package rxinvoice;

import com.google.common.base.Charsets;
import restx.SignatureKey;
import restx.factory.Module;
import restx.factory.Provides;
import restx.jongo.JongoFactory;

import javax.inject.Named;

@Module
public class AppModule {
    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "juma";
    }

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

}
