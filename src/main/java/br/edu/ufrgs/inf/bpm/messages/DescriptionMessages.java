package br.edu.ufrgs.inf.bpm.messages;

public class DescriptionMessages {

    public static String flowNodeWithoutIncoming = "No incoming element was identified in the @elementType @element.";
    public static String flowNodeWithoutOutgoing = "No outgoing element was identified in the @elementType @element.";

    public static String elementWithoutLabel = "No label was identified in the @elementType @element.";
    public static String sequenceFlowWithoutLabel = "Not all condition labels were identified in the @elementType @element.";

    public static String activityWithoutLane = "No resource was identified in the @elementType @element.";

    public static String noStartEvents = "No start event was identified in the process @element.";
    public static String multipleStartEvents = "Multiple start events were identified in the process @element.";

    public static String noEndEvents = "No end event was identified in the process @element.";
    public static String multipleEndEvents = "Multiple end events were identified in the process @element.";

    public static String amountElementsExceed = "The amount of elements in the process @element exceeded the maximum expected quantity (found @amountFind and is expected at most @amountLimit)";
    public static String hasOrGateway = "The element @element is a inclusive gateway. It is a good practice to avoid using this element in a process.";

}
