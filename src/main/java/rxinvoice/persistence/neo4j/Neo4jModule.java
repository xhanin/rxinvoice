package rxinvoice.persistence.neo4j;

import neo4j.GraphDbProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import restx.factory.Module;
import restx.factory.Provides;

import javax.inject.Named;
import java.io.File;

/**
 * Date: 5/10/13
 * Time: 19:02
 */
@Module
public class Neo4jModule {
    @Provides
    public GraphDatabaseService graph() {
        return GraphDbProvider.db();
    }
}
