package br.edu.ufrgs.inf.bpm.wrapper;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class StardogWrapper {

    private static Stardog aStardog;
    private Path dataFile;
    private Path constraintsFile;
    private String dbName;
    private String username;
    private String password;

    public StardogWrapper() throws IOException {
        StartStardog();
        createConnection();
    }

    public static void StartStardog() {
        if (aStardog == null) {
            aStardog = Stardog.builder().create();
        }
    }

    public static void FinishStardog() {
        aStardog.shutdown();
    }

    private void createConnection() throws IOException {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            dataFile = Paths.get(br.edu.ufrgs.inf.bpm.util.Paths.DataFile);
            constraintsFile = Paths.get(br.edu.ufrgs.inf.bpm.util.Paths.ConstraintFile);
            dbName = "dbName";

            input = new FileInputStream(br.edu.ufrgs.inf.bpm.util.Paths.stardogServerProperties);
            prop.load(input);
            username = prop.getProperty("username");
            password = prop.getProperty("password");
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public String getValidation(String instances) {
        addInstances(instances);
        return getValidation();
    }

    public String getValidation() {
        String validation;
        // Open an `AdminConnection` to Stardog so we can set up our database for the example
        try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toEmbeddedServer().credentials(username, password).connect()) {
            // If the example database exists, drop it, so we can create it fresh
            if (aAdminConnection.list().contains(dbName)) {
                aAdminConnection.drop(dbName);
            }

            // create a database
            aAdminConnection.disk(dbName).create(dataFile);

            // Obtain a connection to the database
            try (Connection aConn = ConnectionConfiguration.to(dbName).reasoning(true).credentials(username, password).connect()) {
                ICVConnection aValidator = aConn.as(ICVConnection.class);
                addConstraints(aValidator);
                validation = validate(aValidator);
            } finally {
                // remove the database
                if (aAdminConnection.list().contains(dbName)) {
                    aAdminConnection.drop(dbName);
                }
            }
        }
        return validation;
    }

    private void addInstances(String instances) {

    }

    private void addConstraints(ICVConnection aValidator) {
        aValidator.begin();
        aValidator.addConstraints().format(RDFFormat.TURTLE).file(constraintsFile);
        aValidator.commit();
    }

    private String validate(ICVConnection aValidator) {
        StringBuilder s = new StringBuilder();

        // use namespaces to pretty print results
        Namespaces aNamespaces = aValidator.namespaces();
        // check validity
        boolean isValid = aValidator.isValid();
        // print validation result
        s.append(String.format("The Data is%s valid%n", isValid ? "" : " NOT"));
        // if not valid print explanations
        if (!isValid) {
            Iterable<Proof> aViolationProofs = aValidator.explain().countLimit(10).mergeExplanations().proofs();
            for (Proof aProof : aViolationProofs) {
                s.append("\n").append(ProofWriter.toString(aNamespaces, aProof));
            }
        }

        return s.toString();
    }

}
