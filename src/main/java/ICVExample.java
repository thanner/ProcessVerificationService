/*
 * Copyright (c) 2010-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.complexible.common.base.CloseableIterator;
import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.Constraint;
import com.complexible.stardog.icv.ConstraintFactory;
import com.complexible.stardog.icv.ConstraintViolation;
import com.complexible.stardog.icv.api.ICVConnection;
import com.complexible.stardog.reasoning.Proof;
import com.complexible.stardog.reasoning.ProofWriter;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;

import static com.complexible.common.openrdf.util.ExpressionFactory.some;
import static com.complexible.common.openrdf.util.ExpressionFactory.subClassOf;

/**
 * <p></p>
 *
 * @author Michael Grove
 * @version 4.0
 * @since 0.7
 */
public class ICVExample {

    // Using the Stardog ICV API
    // ---
    // Basic example of how to use Stardog's support for [integrity constraints](http://docs.stardog.com/#_validating_constraints)
    // via the manual validation support offered by an ICVConnection.
    public static void main(String[] args) throws Exception {
        // First need to initialize the Stardog instance which will automatically start the embedded server.
        Stardog aStardog = Stardog.builder().create();

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
                try (Connection aConn = ConnectionConfiguration
                        .to("testICVExample")
                        .reasoning(true)
                        .credentials("admin", "admin")
                        .connect()) {
                    IRI Engine = Values.iri("urn:Engine");
                    IRI Product = Values.iri("urn:Product");
                    IRI Manufacturer = Values.iri("urn:Manufacturer");
                    IRI manufacturedBy = Values.iri("urn:manufacturedBy");
                    IRI e1 = Values.iri("urn:e1");
                    IRI m1 = Values.iri("urn:m1");

                    Model aGraph = Models2.newModel();

                    // Let's create a very simple piece of data, complete with a bit of schema information, to use
                    aGraph.add(Engine, RDFS.SUBCLASSOF, Product);
                    aGraph.add(e1, RDF.TYPE, Engine);
                    aGraph.add(e1, manufacturedBy, m1);

                    // We'll insert that into the database
                    aConn.begin();
                    aConn.add().graph(aGraph);
                    aConn.commit();


                    // Grab an [ICVConnection](http://docs.stardog.com/java/snarl/com/complexible/stardog/icv/api/ICVConnection.html)
                    // so we can add our constraint to the database and start using ICV.
                    ICVConnection aValidator = aConn.as(ICVConnection.class);


                    // aValidator.begin();

                    // Now let's define a constraint; we want to say that a product must be manufactured by a Manufacturer:
                    Constraint aConstraint = ConstraintFactory.constraint(subClassOf(Product, some(manufacturedBy, Manufacturer)));
                    // Add the constraint we just created to our database
                    aValidator.addConstraint(aConstraint);

                    // aValidator.commit();

                    // So we can check whether or not our data is valid,
                    // which it isn't; we're lacking the assertion that m1 is a Manufacturer.
                    System.out.println("The data " + (aValidator.isValid(ContextSets.DEFAULT_ONLY)
                            ? "is"
                            : "is NOT") + " valid!");

                    /**
                    System.out.println("Violations:");
                    CloseableIterator<ConstraintViolation<BindingSet>> violationsMax = aValidator.getViolationBindings();
                    while(violationsMax.hasNext()){
                        System.out.println("Max");
                        CloseableIterator<BindingSet> violations = violationsMax.next().getViolations();
                        while(violations.hasNext()){
                            System.out.println("Min");
                            System.out.println(violations.next().toString());
                            // Tem constraint no violations
                        }
                    }
                    **/

                    // Ok, so our data is invalid.  But what's wrong with it?  It's easy to see in this case, but if we have
                    // a lot of data, it may not be so clear what we're missing.  So lets ask!  Like with reasoning, we
                    // can get a [Proof](http://docs.stardog.com/java/snarl/com/complexible/stardog/reasoning/Proof.html) for
                    // integrity constraint violations.
                    // Proof aProof = aValidator.explain(aConstraint).proof();

                    Constraint dbConstraint = aValidator.getConstraints().stream().findFirst().get();
                    Proof aProof = aValidator.explain(dbConstraint).proof();
                    System.out.println(ProofWriter.toString(aProof));

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