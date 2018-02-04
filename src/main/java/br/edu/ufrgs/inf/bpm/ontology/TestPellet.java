package br.edu.ufrgs.inf.bpm.ontology;

/**
 * import com.hp.hpl.jena.ontologyOld.OntClass;
 * import com.hp.hpl.jena.ontologyOld.OntModel;
 * import com.hp.hpl.jena.rdf.model.ModelFactory;
 * import com.hp.hpl.jena.reasoner.ValidityReport;
 * import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
 * import org.mindswap.pellet.jena.PelletReasonerFactory;
 * <p>
 * import java.util.Iterator;
 * <p>
 * public class TestPellet {
 * <p>
 * public static void main(String[] args) {
 * // PelletOptions.USE_UNIQUE_NAME_ASSUMPTION = true;
 * System.out.println("Results with OntModel");
 * System.out.println("---------------------");
 * System.out.println();
 * <p>
 * // ontologyOld that will be used
 * String ont = "file:src/main/resources/OntologyOld/bpmnbase.owl";
 * String ns = "http://www.scch.at/ontologies/bpmn20base.owl#";
 * <p>
 * // create an empty ontologyOld model using Pellet spec
 * OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
 * <p>
 * // read the file
 * model.read( ont );
 * <p>
 * // print validation report
 * ValidityReport report = model.validate();
 * printIterator( report.getReports(), "Validation Results" );
 * <p>
 * // print superclasses using the utility function
 * OntClass c = model.getOntClass( ns + "BaseElement" );
 * printIterator(c.listSuperClasses(), "All super classes of " + c.getLocalName());
 * <p>
 * // OntClass provides function to print *only* the direct subclasses
 * printIterator(c.listSuperClasses(true), "Direct superclasses of " + c.getLocalName());
 * <p>
 * System.out.println();
 * }
 * <p>
 * public static void printIterator(Iterator<?> i, String header) {
 * System.out.println(header);
 * for(int c = 0; c < header.length(); c++)
 * System.out.print("=");
 * System.out.println();
 * if(i.hasNext()) {
 * while (i.hasNext())
 * System.out.println( i.next() );
 * }
 * else {
 * System.out.println("<EMPTY>");
 * }
 * System.out.println();
 * }
 * }
 **/