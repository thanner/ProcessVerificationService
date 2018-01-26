import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import org.openrdf.rio.RDFFormat;

/**
 * Example of using Stardog Integrity Constraint functionality for data validation example described at http://www.w3.org/2012/12/rdf-val/SOTA
 *
 * @author Evren Sirin
 */
public class SOTAExample {

    public static void main(String[] args) throws Exception {

        // the db name
        String sota = "sota";
        String dataFile = "src/main/resources/sota/sota-data.ttl";
        String constraintsFile = "src/main/resources/sota/sota-constraints.ttl";

        // first create a temporary database to use
        // (if there is already a database with such a name, drop it first)
        // Stardog should be running on the same machine locally for this example
        AdminConnection aAdminConn = AdminConnectionConfiguration.toEmbeddedServer().credentials("admin", "admin").connect();

        if (aAdminConn.list().contains(sota)) {
            aAdminConn.drop(sota);
        }

        // Load the data in the db while creating it
        ConnectionConfiguration aConfig = aAdminConn.memory(sota).create(Paths.get(dataFile));

        // obtain a connection to the database
        Connection aConn = aConfig.connect();

        // ok, we have a database, now need the validator
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
        System.out.format("Data is%s valid%n", isValid ? "" : " NOT");

        // if not valid print explanations
        if (!isValid) {
            Iterable<Proof> aViolationProofs = aValidator.explain().countLimit(10).mergeExplanations().proofs();

            for (Proof aProof : aViolationProofs) {
                System.out.println(ProofWriter.toString(aNamespaces, aProof));
            }
        }

        // always close your connections when you're done
        aConn.close();

        aAdminConn.close();
    }
}