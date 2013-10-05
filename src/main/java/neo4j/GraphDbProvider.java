package neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Provides the neo4j graph database.
 *
 * This is not part of the Neo4j module because it should be loaded only once for the jvm,
 * and restx dev mode makes it impossible currently for classes which are hot compiled (part of the base package).
 *
 * Date: 5/10/13
 * Time: 23:19
 */
public class GraphDbProvider {
    private static GraphDatabaseService graphDb;

    static {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("db/neo4j");
        registerShutdownHook();
    }

    public static GraphDatabaseService db() {
        return graphDb;
    }

    private static void registerShutdownHook() {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
