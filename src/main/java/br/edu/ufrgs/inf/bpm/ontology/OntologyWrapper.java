package br.edu.ufrgs.inf.bpm.ontology;

/**
 * import br.edu.ufrgs.inf.bpm.util.Paths;
 * import org.semanticweb.owlapi.apibinding.OWLManager;
 * import org.semanticweb.owlapi.model.*;
 * import org.semanticweb.owlapi.reasoner.NodeSet;
 * import org.semanticweb.owlapi.reasoner.OWLReasoner;
 * import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
 * import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
 * import org.semanticweb.owlapi.search.EntitySearcher;
 * import org.semanticweb.owlapi.util.DefaultPrefixManager;
 * <p>
 * import java.io.File;
 * import java.util.ArrayList;
 * import java.util.List;
 * import java.util.function.Predicate;
 * import java.util.stream.Collectors;
 * <p>
 * public class OntologyWrapper {
 * <p>
 * private static OWLOntologyManager manager;
 * private static String ontologyIRI;
 * private static OWLOntology ontologyOld;
 * private static OWLDataFactory factory;
 * private static PrefixManager prefixManager;
 * <p>
 * public static void init() {
 * long _start = System.currentTimeMillis();
 * try {
 * if (manager == null) {
 * manager = OWLManager.createOWLOntologyManager();
 * String filePath = Paths.resourcesPath + Paths.ontologyPath;
 * File file = new File(filePath);
 * ontologyOld = manager.loadOntologyFromOntologyDocument(file);
 * factory = manager.getOWLDataFactory();
 * prefixManager = new DefaultPrefixManager(null, null, filePath);
 * // ontologyIRI = IRI.create(file);
 * ontologyIRI = "http://www.scch.at/ontologies/bpmn20base.owl#";
 * prefixManager = new DefaultPrefixManager(null, null, ontologyIRI.toString());
 * }
 * } catch (Exception e) {
 * e.printStackTrace();
 * }
 * System.out.println("Loaded OntologyOld in " + (System.currentTimeMillis() - _start) + "ms.");
 * }
 * <p>
 * public static List<OWLClass> getClass(Predicate<? super OWLClass> predicate) {
 * return ontologyOld.classesInSignature().filter(predicate).collect(Collectors.toList());
 * }
 * <p>
 * public static List<OWLClass> getClasses() {
 * return ontologyOld.classesInSignature().collect(Collectors.toList());
 * }
 * <p>
 * public static void printClasses() {
 * System.out.println("\n Classes:");
 * getClasses().forEach(System.out::println);
 * }
 * <p>
 * public static List<OWLIndividual> getIndividuals() {
 * return ontologyOld.individualsInSignature().collect(Collectors.toList());
 * }
 * <p>
 * // TODO: Arrumar
 * public static List<OWLIndividual> getIndividualsClass(String className) {
 * OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
 * <p>
 * for (OWLClass owlClass : ontologyOld.classesInSignature().collect(Collectors.toList())) {
 * if (owlClass.getIRI().getFragment().equals(className)) {
 * System.out.println("\nClass name: " + owlClass.getIRI().getShortForm());
 * System.out.println("IRI Class: " + owlClass);
 * System.out.println("-----------------------");
 * OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyOld);
 * NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(owlClass, true);
 * System.out.println("Individuals: ");
 * for (OWLNamedIndividual i : instances.entities().collect(Collectors.toList())) {
 * System.out.println(i.getIRI().getFragment());
 * }
 * }
 * }
 * return null;
 * }
 * <p>
 * // TODO: Fazer
 * public static List<ProcessElement> getIndividualsAndPropertiesClass(String s) {
 * List<ProcessElement> individuals = new ArrayList<>();
 * <p>
 * return individuals;
 * }
 * <p>
 * public static void printIndividuals() {
 * System.out.println("\nIndividuals:");
 * getIndividuals().forEach(System.out::println);
 * }
 * <p>
 * public static void addIndividual(ElementType elementType, String elementName) {
 * addIndividual(elementType.toString(), elementName);
 * }
 * <p>
 * private static void addIndividual(String className, String elementName) {
 * //OWLClass owlClass = factory.getOWLClass(":" + className, prefixManager);
 * <p>
 * // OWLNamedIndividual instance = factory.getOWLNamedIndividual(":" + elementName, prefixManager);
 * <p>
 * //OWLNamedIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + elementName));
 * <p>
 * // OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(owlClass, instance);
 * // manager.addAxiom(ontologyOld, classAssertion);
 * <p>
 * OWLIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + elementName));
 * OWLClass owlClass = factory.getOWLClass(IRI.create(ontologyIRI + "#" + className));
 * OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, instance);
 * manager.addAxiom(ontologyOld, axiom);
 * <p>
 * /**
 * try {
 * manager.saveOntology(ontologyOld, new SystemOutDocumentTarget());
 * } catch (OWLOntologyStorageException e) {
 * e.printStackTrace();
 * }
 * <p>
 * /**
 * try {
 * manager.saveOntology(ontologyOld, new StreamDocumentTarget(System.out));
 * } catch (OWLOntologyStorageException e) {
 * e.printStackTrace();
 * }
 * <p>
 * /** Teste para verificar se precisa salvar a ontologia: https://github.com/owlcs/owlapi/blob/version4/contract/src/test/java/uk/ac/manchester/owl/owlapi/tutorialowled2011/TutorialSnippetsTestCase.java
 * try {
 * OWLOntology o = manager.createOntology(IRI.create("Teste.owl"));
 * // Add the class assertion
 * manager.addAxiom(o, classAssertion);
 * // Dump the ontologyOld
 * StreamDocumentTarget target = new StreamDocumentTarget(new ByteArrayOutputStream());
 * manager.saveOntology(o, target);
 * } catch(Exception e) {
 * e.printStackTrace();
 * }
 * }
 * <p>
 * public static List<OWLDataProperty> getDataProperties() {
 * return ontologyOld.dataPropertiesInSignature().collect(Collectors.toList());
 * }
 * <p>
 * public static void printDataProperties() {
 * System.out.println("\nData Properties:");
 * getDataProperties().forEach(System.out::println);
 * }
 * <p>
 * public static void getDataPropertiesClass(OWLClass className) {
 * System.out.println("\nData Property Domain:");
 * for (OWLDataPropertyDomainAxiom dp : ontologyOld.axioms(AxiomType.DATA_PROPERTY_DOMAIN).collect(Collectors.toList())) {
 * if (dp.getDomain().equals(className)) {
 * for (OWLDataProperty odp : dp.dataPropertiesInSignature().collect(Collectors.toList())) {
 * System.out.println("\t\t +: " + odp.getIRI().getShortForm());
 * }
 * System.out.println("\t\t +:" + dp.getProperty());
 * }
 * }
 * }
 * <p>
 * public static List<ProcessElement> getCompleteIndividuals() {
 * List<ProcessElement> individuals = new ArrayList<>();
 * ProcessElement individual;
 * for (OWLClass owlClass : ontologyOld.classesInSignature().collect(Collectors.toList())) {
 * for (OWLIndividual i : EntitySearcher.getIndividuals(owlClass, ontologyOld).collect(Collectors.toList())) {
 * individual = new ProcessElement(owlClass.toString(), i.toString());
 * <p>
 * List<OWLDataProperty> owlDataProperties = i.dataPropertiesInSignature().collect(Collectors.toList());
 * for (OWLDataProperty dataProperty : owlDataProperties) {
 * individual.addDataProperty(dataProperty.getIRI().getShortForm(), dataProperty.toString());
 * }
 * <p>
 * List<OWLObjectProperty> owlObjectProperties = i.objectPropertiesInSignature().collect(Collectors.toList());
 * for (OWLObjectProperty objectProperty : owlObjectProperties) {
 * individual.addObjectProperty(objectProperty.getIRI().getShortForm(), objectProperty.toString());
 * }
 * <p>
 * individuals.add(individual);
 * }
 * }
 * return individuals;
 * }
 * <p>
 * public static void addCompleteIndividual(ProcessElement individual) {
 * OWLNamedIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + individual.getIndividualName()));
 * OWLClass owlClass = factory.getOWLClass(IRI.create(ontologyIRI + individual.getClassName()));
 * <p>
 * OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, instance);
 * manager.addAxiom(ontologyOld, axiom);
 * <p>
 * for (String key : individual.getOwlDataProperties().keySet()) {
 * // OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(ontologyIRI + key));
 * <p>
 * OWLDataProperty dataProperty = factory.getOWLDataProperty(":" + key, prefixManager);
 * OWLLiteral value = factory.getOWLLiteral(individual.getOWLDataProperty(key));
 * OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, instance, value);
 * <p>
 * manager.addAxiom(ontologyOld, dataPropertyAssertion);
 * }
 * <p>
 * for (String key : individual.getOwlObjectProperties().keySet()) {
 * // Chave key
 * }
 * }
 * }
 **/