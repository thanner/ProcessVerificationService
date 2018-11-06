package br.edu.ufrgs.inf.bpm.messages;

public enum MessageType {
    STRUCTURE("structure"), LABEL("label"), PRAGMATIC("pragmatic");

    private final String messageType;

    MessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getValue() {
        return messageType;
    }

}
