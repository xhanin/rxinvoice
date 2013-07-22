package rxinvoice;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.jongo.Jongo;
import org.jongo.Mapper;
import restx.factory.*;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Overrides restx jongo factory to allow to customize mongo DB used.
 * This behaviour will later be moved to restx-jongo.
 *
 * You can use system properties to customize this, eg
 * -Dmongo.host=dharma.mongohq.com -Dmongo.port=10002 -Dmongo.user=rxinvoice -Dmongo.password=XXXXXXXXX
 */
@Machine
public class JongoFactory extends SingleNameFactoryMachine<Jongo> {
    public static final String JONGO_DB_NAME = "mongo.db";
    public static final String JONGO_DB_HOST = "mongo.host";
    public static final String JONGO_DB_PORT = "mongo.port";
    public static final String JONGO_DB_USER = "mongo.user";
    public static final String JONGO_DB_PASSWORD = "mongo.password";

    public static final Name<String> JONGO_DB = Name.of(String.class, JONGO_DB_NAME);
    public static final Name<Jongo> NAME = Name.of(Jongo.class, "Jongo");

    public JongoFactory() {
        super(-10, new MachineEngine<Jongo>() {
            private Factory.Query<String> dbNameQuery = Factory.Query.byName(JONGO_DB);
            private Factory.Query<String> dbHostQuery = Factory.Query.byName(Name.of(String.class, JONGO_DB_HOST)).optional();
            private Factory.Query<String> dbPortQuery = Factory.Query.byName(Name.of(String.class, JONGO_DB_PORT)).optional();
            private Factory.Query<String> dbUserQuery = Factory.Query.byName(Name.of(String.class, JONGO_DB_USER)).optional();
            private Factory.Query<String> dbPasswordQuery = Factory.Query.byName(Name.of(String.class, JONGO_DB_PASSWORD)).optional();
            private Factory.Query<Mapper> mapperQuery = Factory.Query.byClass(Mapper.class);

            @Override
            public Name<Jongo> getName() {
                return NAME;
            }

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return BillOfMaterials.of(
                        dbNameQuery, dbHostQuery, dbPortQuery, dbUserQuery, dbPasswordQuery,
                        mapperQuery);
            }

            public ComponentBox<Jongo> newComponent(SatisfiedBOM satisfiedBOM) {
                return new BoundlessComponentBox<Jongo>(
                        new NamedComponent(NAME, doNewComponent(satisfiedBOM))) {
                    @Override
                    public void close() {
                        pick().get().getComponent().getDatabase().getMongo().close();
                    }
                };
            }

            public Jongo doNewComponent(SatisfiedBOM satisfiedBOM) {
                String db = satisfiedBOM.getOne(dbNameQuery).get().getComponent();
                String host = getStringOrDefault(satisfiedBOM, dbHostQuery, JONGO_DB_HOST, "localhost");
                int port = Integer.parseInt(getStringOrDefault(satisfiedBOM, dbPortQuery, JONGO_DB_PORT, "27017"));
                String user = getStringOrDefault(satisfiedBOM, dbUserQuery, JONGO_DB_USER, "");
                String password = getStringOrDefault(satisfiedBOM, dbPasswordQuery, JONGO_DB_PASSWORD, "");
                try {
                    MongoClient mongoClient;
                    if (Strings.isNullOrEmpty(user)) {
                        mongoClient = new MongoClient(host, port);
                    } else {
                        mongoClient = new MongoClient(
                                new ServerAddress(host, port),
                                Arrays.asList(MongoCredential.createMongoCRCredential(user, db, password.toCharArray())));
                    }
                    return new Jongo(mongoClient.getDB(db),
                            satisfiedBOM.getOne(mapperQuery).get().getComponent());
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return "JongoFactoryMachineEngine";
            }

        });
    }

    private static String getStringOrDefault(
            SatisfiedBOM satisfiedBOM, Factory.Query<String> query, String name, String defValue) {
        return satisfiedBOM.getOne(query).or(
                NamedComponent.of(String.class, name, defValue)).getComponent();
    }
}
