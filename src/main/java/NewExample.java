import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.Constraint;
import com.complexible.stardog.icv.ConstraintFactory;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;

import java.nio.file.Paths;

import static com.complexible.common.openrdf.util.ExpressionFactory.some;
import static com.complexible.common.openrdf.util.ExpressionFactory.subClassOf;

public class NewExample {

    public static void main(String[] args) throws Exception {
        // First need to initialize the Stardog instance which will automatically start the embedded server.
        Stardog aStardog = Stardog.builder().create();

        // The db name
        String dbName = "sota";
        String dataFile = "src/main/resources/sota/sota-data.ttl";
        String constraintsFile = "src/main/resources/sota/sota-constraints.ttl";

        try {
            // Open an `AdminConnection` to Stardog so we can set up our database for the example
            try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer()
                    .credentials("admin", "admin")
                    .connect()) {
                // If the example database exists, drop it, so we can create it fresh
                if (aAdminConnection.list().contains("testICVExample")) {
                    aAdminConnection.drop("testICVExample");
                }

                // create a disk database
                aAdminConnection.disk("testICVExample").create();

                // Obtain a connection to the database
                try (Connection aConns = ConnectionConfiguration
                        .to("testICVExample")
                        .reasoning(true)
                        .credentials("admin", "admin")
                        .connect()) {

                    // Load the data in the db while creating it
                    ConnectionConfiguration aConfig = aAdminConnection.memory(dbName).create(Paths.get(dataFile));
                    // obtain a connection to the database
                    Connection aConn = aConfig.connect();

                    // Grab an [ICVConnection](http://docs.stardog.com/java/snarl/com/complexible/stardog/icv/api/ICVConnection.html)
                    // so we can add our constraint to the database and start using ICV.
                    ICVConnection aValidator = aConn.as(ICVConnection.class);

                    // add the constraints, must do this in a transaction
                    aValidator.begin();
                    aValidator.addConstraints().format(RDFFormat.TURTLE).file(Paths.get(constraintsFile));
                    aValidator.commit();

                    // use namespaces to pretty print results
                    Namespaces aNamespaces = aValidator.namespaces();

                    // check validity
                    boolean isValid = aValidator.isValid();

                    // print validation result
                    System.out.format("The Data is%s valid%n", isValid ? "" : " NOT");

                    // if not valid print explanations
                    if (!isValid) {
                        Iterable<Proof> aViolationProofs = aValidator.explain().countLimit(10).mergeExplanations().proofs();

                        for (Proof aProof : aViolationProofs) {
                            System.out.println(ProofWriter.toString(aNamespaces, aProof));
                        }
                    }

                }
                finally {
                    // remove the disk database
                    if (aAdminConnection.list().contains("testICVExample")) {
                        aAdminConnection.drop("testICVExample");
                    }
                }
            }
        }
        finally {
            aStardog.shutdown();
        }
    }
}