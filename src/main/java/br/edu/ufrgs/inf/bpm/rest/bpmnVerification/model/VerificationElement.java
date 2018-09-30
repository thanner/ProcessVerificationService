/*
package br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public class VerificationElement {

    private String id;
    private String name;
    private Set<Message> messages;

    public VerificationElement() {
        this.messages = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMessage(String elementId, String description) {
        Message message = new Message();
        message.setElementId(elementId);
        message.setDescription(description);
        this.messages.add(message);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Id: ").append(id).append("\n");

        stringBuilder.append("Name: ").append(name).append("\n");
        stringBuilder.append("Messages:").append("\n");
        for (Message message : messages) {
            stringBuilder.append("\t").append(message.getElementId()).append(" ").append(message.getDescription()).append("\n");
        }

        return stringBuilder.toString();
    }

}
*/