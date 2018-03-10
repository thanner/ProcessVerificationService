package br.edu.ufrgs.inf.bpm.ontology;

import br.edu.ufrgs.inf.bpm.util.Paths;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OntologyWrapper {

    private static OWLOntologyManager manager;
    private static String ontologyIRI;
    private static OWLOntology ontologyOld;
    private static OWLDataFactory factory;
    private static PrefixManager prefixManager;

    public static void init() {
        long _start = System.currentTimeMillis();
        try {
            if (manager == null) {
                manager = OWLManager.createOWLOntologyManager();
                String filePath = Paths.LocalResourcePath + Paths.OntologyPath;
                File file = new File(filePath);
                ontologyOld = manager.loadOntologyFromOntologyDocument(file);
                factory = manager.getOWLDataFactory();
                // prefixManager = new DefaultPrefixManager(null, null, filePath);
                // ontologyIRI = IRI.create(file);
                ontologyIRI = "http://www.scch.at/ontologies/bpmn20base.owl#";
                prefixManager = new DefaultPrefixManager(ontologyIRI.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loaded OntologyOld in " + (System.currentTimeMillis() - _start) + "ms.");
    }

    public static List<OWLClass> getClass(Predicate<? super OWLClass> predicate) {
        return ontologyOld.getClassesInSignature().stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<OWLClass> getClasses() {
        return ontologyOld.getClassesInSignature().stream().collect(Collectors.toList());
    }

    public static void printClasses() {
        System.out.println("\n Classes:");
        getClasses().forEach(System.out::println);
    }

    public static List<OWLIndividual> getIndividuals() {
        return new ArrayList<>(ontologyOld.getIndividualsInSignature());
    }

    // TODO: Arrumar
    public static List<OWLIndividual> getIndividualsClass(String className) {
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

        for (OWLClass owlClass : new ArrayList<>(ontologyOld.getClassesInSignature())) {
            if (owlClass.getIRI().getFragment().equals(className)) {
                System.out.println("\nClass name: " + owlClass.getIRI());
                System.out.println("IRI Class: " + owlClass);
                System.out.println("-----------------------");
                OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyOld);
                NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(owlClass, true);
                System.out.println("Individuals: ");
                for (OWLNamedIndividual i : instances.getFlattened()) {
                    System.out.println(i.getIRI().getFragment());
                }
            }
        }
        return null;
    }

    public static void printIndividuals() {
        System.out.println("\nIndividuals:");
        getIndividuals().forEach(System.out::println);
    }

    public static void addIndividual(ElementType elementType, String elementName) {
        addIndividual(elementType.toString(), elementName);
    }

    private static void addIndividual(String className, String elementName) {
        // OWLClass owlClass = factory.getOWLClass(":" + className, prefixManager);
        // OWLNamedIndividual instance = factory.getOWLNamedIndividual(":" + elementName, prefixManager);
        // OWLNamedIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + elementName));
        // OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(owlClass, instance);
        // manager.addAxiom(ontologyOld, classAssertion);

        OWLIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + elementName));
        OWLClass owlClass = factory.getOWLClass(IRI.create(ontologyIRI + "#" + className));
        OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, instance);
        manager.addAxiom(ontologyOld, axiom);

        try {
            manager.saveOntology(ontologyOld, new StreamDocumentTarget(System.out));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }

        // Teste para verificar se precisa salvar a ontologia:
        // https://github.com/owlcs/owlapi/blob/version4/contract/src/test/java/uk/ac/manchester/owl/owlapi/tutorialowled2011/TutorialSnippetsTestCase.java
        /*
        try {
            OWLOntology o = manager.createOntology(IRI.create("Teste.owl"));
            // Add the class assertion
            manager.addAxiom(o, classAssertion);
            // Dump the ontologyOld
            StreamDocumentTarget target = new StreamDocumentTarget(new ByteArrayOutputStream());
            manager.saveOntology(o, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public static List<OWLDataProperty> getDataProperties() {
        return new ArrayList<>(ontologyOld.getDataPropertiesInSignature());
    }

    public static void printDataProperties() {
        System.out.println("\nData Properties:");
        getDataProperties().forEach(System.out::println);
    }

    /*
    public static void getDataPropertiesClass(OWLClass className) {
        System.out.println("\nData Property Domain:");
        for (OWLDataPropertyDomainAxiom dp : ontologyOld.getDataPropertyDomainAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
            if (dp.getDomain().equals(className)) {
                for (OWLDataProperty odp : dp.dataPropertiesInSignature().collect(Collectors.toList())) {
                    System.out.println("\t\t +: " + odp.getIRI().getShortForm());
                }
                System.out.println("\t\t +:" + dp.getProperty());
            }
        }
    }
    */

    public static List<ProcessElement> getCompleteIndividuals() {
        List<ProcessElement> individuals = new ArrayList<>();
        ProcessElement individual;
        for (OWLClass owlClass : ontologyOld.getClassesInSignature().stream().collect(Collectors.toList())) {
            for (OWLIndividual i : EntitySearcher.getIndividuals(owlClass, ontologyOld).collect(Collectors.toList())) {
                individual = new ProcessElement(owlClass.toString(), i.toString());

                List<OWLDataProperty> owlDataProperties = new ArrayList<>(i.getDataPropertiesInSignature());
                for (OWLDataProperty dataProperty : owlDataProperties) {
                    individual.addDataProperty(dataProperty.getIRI().toString(), dataProperty.toString());
                }

                List<OWLObjectProperty> owlObjectProperties = new ArrayList<>(i.getObjectPropertiesInSignature());
                for (OWLObjectProperty objectProperty : owlObjectProperties) {
                    individual.addObjectProperty(objectProperty.getIRI().toString(), objectProperty.toString());
                }

                individuals.add(individual);
            }
        }
        return individuals;
    }

    public static void addCompleteIndividual(ProcessElement individual) {
        OWLNamedIndividual instance = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + individual.getIndividualName()));
        OWLClass owlClass = factory.getOWLClass(IRI.create(ontologyIRI + individual.getClassName()));

        OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, instance);
        manager.addAxiom(ontologyOld, axiom);

        for (String key : individual.getOwlDataProperties().keySet()) {
            // OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(ontologyIRI + key));

            OWLDataProperty dataProperty = factory.getOWLDataProperty(":" + key, prefixManager);
            OWLLiteral value = factory.getOWLLiteral(individual.getOWLDataProperty(key).toString());
            OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, instance, value);

            manager.addAxiom(ontologyOld, dataPropertyAssertion);
        }

        for (String key : individual.getOwlObjectProperties().keySet()) {
            // Chave key
        }
    }

}