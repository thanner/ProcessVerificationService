import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import org.openrdf.rio.RDFFormat;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NewExample {

    public static void main(String[] args) {
        // First need to initialize the Stardog instance which will automatically start the embedded server.
        Stardog aStardog = Stardog.builder().create();

        // The db name
        String dbName = "sota";
        Path dataFile = Paths.get("src/main/resources/sota/sota-data.ttl");
        Path constraintsFile = Paths.get("src/main/resources/sota/sota-constraints.ttl");
        String username = "admin";
        String password = "admin";

        try {
            // Open an `AdminConnection` to Stardog so we can set up our database for the example
            try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer().credentials(username, password).connect()) {
                // If the example database exists, drop it, so we can create it fresh
                if (aAdminConnection.list().contains(dbName)) {
                    aAdminConnection.drop(dbName);
                }

                // create a disk database
                aAdminConnection.disk(dbName).create(dataFile);

                // Obtain a connection to the database
                try (Connection aConn = ConnectionConfiguration.to(dbName).reasoning(true).credentials(username, password).connect()) {

                    /** Method: Constraints **/
                    // Grab an [ICVConnection](http://docs.stardog.com/java/snarl/com/complexible/stardog/icv/api/ICVConnection.html)
                    // so we can add our constraint to the database and start using ICV.
                    ICVConnection aValidator = aConn.as(ICVConnection.class);
                    // add the constraints, must do this in a transaction
                    aValidator.begin();
                    aValidator.addConstraints().format(RDFFormat.TURTLE).file(constraintsFile);
                    aValidator.commit();

                    /** Method: Validate **/
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
                    if (aAdminConnection.list().contains(dbName)) {
                        aAdminConnection.drop(dbName);
                    }
                }
            }
        }
        finally {
            aStardog.shutdown();
        }
    }
}