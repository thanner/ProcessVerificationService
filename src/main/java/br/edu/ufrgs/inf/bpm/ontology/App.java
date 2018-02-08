package br.edu.ufrgs.inf.bpm.ontology;

import java.util.List;

public class App {

    public static void main(String[] args) {

        // OntologyOld Test
        OntologyWrapper.init();

        // Creating Individual
        ProcessElement individual = new ProcessElement(ElementType.STARTEVENT);
        individual.addDataProperty("name", "ValorDataPropertyName");

        // Add and get individual
        OntologyWrapper.addCompleteIndividual(individual);
        List<ProcessElement> individuals = OntologyWrapper.getCompleteIndividuals();
        individuals.forEach(System.out::println);

    }
}