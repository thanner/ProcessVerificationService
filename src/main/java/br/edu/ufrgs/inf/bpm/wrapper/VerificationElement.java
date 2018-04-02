package br.edu.ufrgs.inf.bpm.wrapper;

import java.util.ArrayList;
import java.util.List;

public class VerificationElement {

    private String id;
    private String name;
    private List<String> messages;

    public VerificationElement() {
        this.messages = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ").append(id).append("\n");
        stringBuilder.append("Name: ").append(name).append("\n");

        stringBuilder.append("Messages:").append("\n");
        for (String message : messages) {
            stringBuilder.append("\t").append(message).append("\n");
        }

        return stringBuilder.toString();
    }
}
