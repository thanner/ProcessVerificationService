package br.edu.ufrgs.inf.bpm.ontology;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.HashMap;
import java.util.Map;

public class ProcessElement {

    private String individualName;
    private String className;
    private Map<String, OWLDataProperty> owlDataProperties = new HashMap<>();
    private Map<String, OWLObjectProperty> owlObjectProperties = new HashMap<>();

    public ProcessElement(ElementType elementType) {
    }

    public ProcessElement(String string, String string1) {
    }

    public String getIndividualName() {
        return individualName;
    }

    public String getClassName() {
        return className;
    }

    public OWLDataProperty getOWLDataProperty(String key) {
        return owlDataProperties.get(key);
    }

    public Map<String, OWLDataProperty> getOwlDataProperties() {
        return owlDataProperties;
    }

    public OWLObjectProperty getOWLObjectProperty(String key) {
        return owlObjectProperties.get(key);
    }

    public Map<String, OWLObjectProperty> getOwlObjectProperties() {
        return owlObjectProperties;
    }

    public void addDataProperty(String name, String dataPropertyValue) {
    }


    public void addObjectProperty(String name, String value) {
    }

}
