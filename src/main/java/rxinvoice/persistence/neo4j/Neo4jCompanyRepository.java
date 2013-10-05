package rxinvoice.persistence.neo4j;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.neo4j.graphdb.*;
import org.neo4j.tooling.GlobalGraphOperations;
import restx.common.UUIDGenerator;
import restx.factory.Component;
import restx.factory.When;
import rxinvoice.domain.Address;
import rxinvoice.domain.Company;
import rxinvoice.persistence.CompanyRepository;

import javax.inject.Named;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Date: 5/10/13
 * Time: 19:06
 */
@Component
@When(name = "persistence", value = "neo4j")
public class Neo4jCompanyRepository implements CompanyRepository {
    private final GraphDatabaseService graphDb;

    public Neo4jCompanyRepository(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    @Override
    public Iterable<Company> findCompanies() {
        try (Transaction tx = graphDb.beginTx()) {
            return newArrayList(transform(
                    GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(DynamicLabel.label("Company")),
                    new Function<Node, Company>() {
                        @Override
                        public Company apply(Node node) {
                            return toCompany(node);
                        }
                    }));
        }
    }

    @Override
    public Optional<Company> findCompanyByKey(String key) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = getNodeById(key);
            if (node == null) {
                return Optional.absent();
            }

            return Optional.of(toCompany(node));
        }
    }

    @Override
    public Company createCompany(Company company) {
        try ( Transaction tx = graphDb.beginTx() ) {
            Node node = graphDb.createNode(DynamicLabel.label("Company"));
            toNode(company.setKey(String.valueOf(node.getId())), node);
            tx.success();
        }

        return company;
    }

    @Override
    public Company updateCompany(Company company) {
        try ( Transaction tx = graphDb.beginTx() ) {
            toNode(company, getNodeById(company.getKey()));
            tx.success();
        }

        return company;
    }

    @Override
    public void deleteCompany(String key) {
        try ( Transaction tx = graphDb.beginTx() ) {
            getNodeById(key).delete();
            tx.success();
        }
    }

    private Node getNodeById(String key) {
        try {
            return graphDb.getNodeById(Long.parseLong(key));
        } catch (NotFoundException e) {
            return null;
        }
    }

    private Company toCompany(Node node) {
        return new Company()
                .setKey(String.valueOf(node.getId()))
                .setName((String) node.getProperty("name"))
                .setAddress(new Address()
                        .setBody((String) node.getProperty("addrBody"))
                        .setZipCode((String) node.getProperty("zipCode"))
                        .setCity((String) node.getProperty("city"))
                );
    }

    private void toNode(Company company, Node node) {
        node.setProperty("name", company.getName());
        node.setProperty("addrBody", company.getAddress().getBody());
        node.setProperty("zipCode", company.getAddress().getZipCode());
        node.setProperty("city", company.getAddress().getCity());
    }
}
