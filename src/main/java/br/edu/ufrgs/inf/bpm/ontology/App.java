package br.edu.ufrgs.inf.bpm.ontology;

/**
 * import br.edu.ufrgs.inf.bpm.bpmparser.communication.ElementType;
 * import br.edu.ufrgs.inf.bpm.bpmparser.communication.ProcessElement;
 * <p>
 * import java.util.List;
 * <p>
 * public class App {
 * <p>
 * public static void main(String[] args) {
 * <p>
 * // OntologyOld Test
 * OntologyWrapper.init();
 * <p>
 * // Creating Individual
 * ProcessElement individual = new ProcessElement(ElementType.STARTEVENT);
 * individual.addDataProperty("name", "ValorDataPropertyName");
 * <p>
 * // Add and get individual
 * OntologyWrapper.addCompleteIndividual(individual);
 * List<ProcessElement> individuals = OntologyWrapper.getCompleteIndividuals();
 * individuals.forEach(System.out::println);
 * <p>
 * // WordNet Test
 * /**
 * try {
 * JWNL.initialize(new FileInputStream(Paths.resourcesPath + Paths.wordNetPath));
 * final Dictionary dictionary = Dictionary.getInstance();
 * IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "blue");
 * Synset[] senses = indexWord.getSenses();
 * for (Synset set : senses) {
 * System.out.println(indexWord + ": " + set.getGloss());
 * }
 * } catch(Exception e) {
 * e.printStackTrace();
 * }
 * <p>
 * }
 * }
 **/