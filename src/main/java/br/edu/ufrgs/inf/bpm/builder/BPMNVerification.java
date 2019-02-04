package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.messages.DescriptionMessages;
import br.edu.ufrgs.inf.bpm.messages.MessageType;
import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.MessageHandler;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class BPMNVerification {

    List<TMessage> verificationMessages;
    BpmnWrapper bpmnWrapper;
    String source = "BPMN Verification";
    MessageHandler messageHandler;
    private int amountElementsLimit = 30;

    public BPMNVerification() {
        this.messageHandler = new MessageHandler();
    }

    public List<TMessage> verify(TDefinitions tDefinitions) {
        verificationMessages = new ArrayList<>();
        bpmnWrapper = new BpmnWrapper(tDefinitions);

        for (TProcess tProcess : bpmnWrapper.getProcessList()) {
            verifyFlowNodesWithoutIncomings(tProcess);
            verifyFlowNodesWithoutOutgoings(tProcess);
            verifyAmountStartEvents(tProcess);
            verifyAmountEndEvents(tProcess);
            verifyElementsWithoutLabels(tProcess);
            verifyActivitiesWithoutLane(tProcess);
            verifyAmountElements(tProcess);
            verifyORGateways(tProcess);
        }

        return verificationMessages;
    }

    private void verifyFlowNodesWithoutIncomings(TProcess tProcess) {
        List<TFlowNode> tFlowNodeWithoutIncoming = bpmnWrapper.getFlowNodesWithoutIncoming(tProcess);
        for (TFlowNode tFlowNode : tFlowNodeWithoutIncoming) {
            if (!(tFlowNode instanceof TStartEvent)) {
                String elementType = messageHandler.getElementType(tFlowNode);
                String description = generateDescriptionElementType(DescriptionMessages.flowNodeWithoutIncoming, getElementRepresentation(tFlowNode), elementType);
                createMessage(getElementIdRepresentation(tFlowNode), description, MessageType.STRUCTURE);
            }
        }
    }

    private void verifyFlowNodesWithoutOutgoings(TProcess tProcess) {
        List<TFlowNode> tFlowNodeWithoutOutgoing = bpmnWrapper.getFlowNodesWithoutOutgoing(tProcess);
        for (TFlowNode tFlowNode : tFlowNodeWithoutOutgoing) {
            if (!(tFlowNode instanceof TEndEvent)) {
                String elementType = messageHandler.getElementType(tFlowNode);
                String description = generateDescriptionElementType(DescriptionMessages.flowNodeWithoutOutgoing, getElementRepresentation(tFlowNode), elementType);
                createMessage(getElementIdRepresentation(tFlowNode), description, MessageType.STRUCTURE);
            }
        }
    }

    private void verifyAmountStartEvents(TProcess tProcess) {
        List<TStartEvent> tStartEventList = bpmnWrapper.getFlowElementList(TStartEvent.class, tProcess);
        int size = tStartEventList.size();
        if (size == 0) {
            createMessage(getElementIdRepresentation(tProcess), generateDescription(DescriptionMessages.noStartEvents, tProcess), MessageType.PRAGMATIC);
        } else if (size > 1) {
            //for (TStartEvent tStartEvent : tStartEventList) {
            createMessage("Process", generateDescription(DescriptionMessages.multipleStartEvents, tProcess), MessageType.PRAGMATIC);
            //}
        }
    }

    private void verifyAmountEndEvents(TProcess tProcess) {
        List<TEndEvent> tEndEventList = bpmnWrapper.getFlowElementList(TEndEvent.class, tProcess);
        int size = tEndEventList.size();
        if (size == 0) {
            createMessage(getElementIdRepresentation(tProcess), generateDescription(DescriptionMessages.noEndEvents, tProcess), MessageType.PRAGMATIC);
        } else if (size > 1) {
            //for (TEndEvent tEndEvent : tEndEventList) {
            createMessage("Process", generateDescription(DescriptionMessages.multipleEndEvents, tProcess), MessageType.PRAGMATIC);
            //}
        }
    }

    private void verifyElementsWithoutLabels(TProcess tProcess) {
        for (TLaneSet laneSet : tProcess.getLaneSet()) {
            for (TLane lane : laneSet.getLane()) {
                verifyElementWithoutLabels(lane, "Lane");
            }
        }

        for (JAXBElement<? extends TFlowElement> flowElementJaxb : tProcess.getFlowElement()) {
            TFlowElement flowElement = flowElementJaxb.getValue();
            String elementType = messageHandler.getElementType(flowElement);
            if (flowElement instanceof TActivity) {
                verifyElementWithoutLabels(flowElement, elementType);
            } else if (flowElement instanceof TEvent) {
                verifyEventWithoutLabels(flowElement, elementType);
            } else if (flowElement instanceof TExclusiveGateway || flowElement instanceof TInclusiveGateway || flowElement instanceof TEventBasedGateway || flowElement instanceof TComplexGateway) {
                if (((TGateway) flowElement).getOutgoing().size() > 1) {
                    verifyElementWithoutLabels(flowElement, elementType);
                    verifySequenceFlowsWithoutLabels((TGateway) flowElement, elementType);
                }
            }
        }

        /*
        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            if (flowElement.getValue() instanceof TSequenceFlow) {
                verifyElementWithoutLabels(flowElement.getValue(), "sequence flow");
            }
        }
        */

        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            if (flowElement.getValue() instanceof TBoundaryEvent) {
                verifyElementWithoutLabels(flowElement.getValue(), "Boundary Event");
            }
        }

    }

    private void verifyElementWithoutLabels(TFlowElement tFlowElement, String elementType) {
        if (tFlowElement.getName() == null || tFlowElement.getName().isEmpty()) {
            createMessage(getElementIdRepresentation(tFlowElement), generateDescriptionElementType(DescriptionMessages.elementWithoutLabel, getElementRepresentation(tFlowElement), elementType), MessageType.LABEL);
        }
    }

    private void verifyEventWithoutLabels(TFlowElement tFlowElement, String elementType) {
        if (tFlowElement.getName() == null || tFlowElement.getName().isEmpty()) {
            createMessage("Process", generateDescriptionElementType(DescriptionMessages.elementWithoutLabel, getElementRepresentation(tFlowElement), elementType), MessageType.LABEL);
        }
    }

    private void verifyElementWithoutLabels(TLane tLane, String elementType) {
        if (tLane.getName() == null || tLane.getName().isEmpty()) {
            createMessage(getElementIdRepresentation(tLane), generateDescriptionElementType(DescriptionMessages.elementWithoutLabel, getElementRepresentation(tLane), elementType), MessageType.LABEL);
        }
    }

    private void verifySequenceFlowsWithoutLabels(TGateway gateway, String elementType) {
        if (gateway instanceof TExclusiveGateway || gateway instanceof TInclusiveGateway) {
            boolean hasEmptyNameSequenceFlow = false;
            for (TSequenceFlow tSequenceFlow : bpmnWrapper.getTargetSequenceFlows(gateway)) {
                if (tSequenceFlow.getName() == null || tSequenceFlow.getName().isEmpty()) {
                    hasEmptyNameSequenceFlow = true;
                }
            }

            if (hasEmptyNameSequenceFlow) {
                createMessage(getElementIdRepresentation(gateway), generateDescriptionElementType(DescriptionMessages.sequenceFlowWithoutLabel, getElementRepresentation(gateway), elementType), MessageType.LABEL);
            }
        }
    }

    private void verifyActivitiesWithoutLane(TProcess tProcess) {
        for (JAXBElement<? extends TFlowElement> jaxbElement : tProcess.getFlowElement()) {
            TFlowElement tFlowElement = jaxbElement.getValue();
            if (tFlowElement instanceof TActivity) {
                TLane tLane = bpmnWrapper.getLaneByFlowElement(tFlowElement);
                if (tLane == null) {
                    String elementType = messageHandler.getElementType(tFlowElement);
                    createMessage(getElementIdRepresentation(tFlowElement), generateDescriptionElementType(DescriptionMessages.activityWithoutLane, getElementRepresentation(tFlowElement), elementType), MessageType.LABEL);
                }
            }
        }

    }

    private void verifyAmountElements(TProcess tProcess) {
        int amountElements = bpmnWrapper.getAmountFlowNode(tProcess);
        if (amountElements > amountElementsLimit) {
            createMessage(getElementIdRepresentation(tProcess), generateDescriptionAmountElements(DescriptionMessages.amountElementsExceed, tProcess, amountElements), MessageType.PRAGMATIC);
        }
    }

    private void verifyORGateways(TProcess tProcess) {
        for (JAXBElement<? extends TFlowElement> tFlowElement : tProcess.getFlowElement()) {
            if (tFlowElement.getValue() instanceof TInclusiveGateway) {
                TInclusiveGateway tInclusiveGateway = (TInclusiveGateway) tFlowElement.getValue();
                createMessage(getElementIdRepresentation(tInclusiveGateway), generateDescription(DescriptionMessages.hasOrGateway, tInclusiveGateway), MessageType.PRAGMATIC);
            }
        }
    }

    private void createMessage(String id, String description, MessageType messageType) {
        TMessage tMessage = new TMessage();
        tMessage.setProcessElementId(id);
        tMessage.setDescription(description);
        tMessage.setMessageType(messageType.getValue());
        tMessage.setSource(source);
        verificationMessages.add(tMessage);
    }

    private String generateDescription(String description, TProcess tProcess) {
        return description.replaceAll("@element", getProcessRepresentation(tProcess));
    }

    private String generateDescription(String description, TFlowElement tFlowElement) {
        return description.replaceAll("@element", getElementRepresentation(tFlowElement));
    }

    private String generateDescriptionElementType(String description, String elementRepresentation, String elementType) {
        return description.replaceAll("@elementType", elementType).replaceAll("@element", elementRepresentation);
    }

    private String generateDescriptionAmountElements(String description, TProcess tProcess, int amountElements) {
        return description.replaceAll("@element", getProcessRepresentation(tProcess)).replaceAll("@amountFind", String.valueOf(amountElements)).replaceAll("@amountLimit", String.valueOf(amountElementsLimit));
    }

    private String getProcessRepresentation(TProcess tProcess) {
        String elementName = getProcessName(tProcess);
        String elementId = getElementId(tProcess);
        return getRepresentation(elementName, elementId, "process");
    }

    private String getElementRepresentation(TLane tLane) {
        String elementName = getLaneName(tLane);
        String elementId = getElementId(tLane);
        return getRepresentation(elementName, elementId, "lane");
    }

    private String getElementRepresentation(TFlowElement tFlowElement) {
        String elementName = getElementName(tFlowElement);
        String elementId = getElementId(tFlowElement);
        return getRepresentation(elementName, elementId, "element");
    }

    private String getRepresentation(String elementName, String elementId, String elementType) {
        String label = "";
        if (elementName != null) {
            label += "\"" + elementName.trim() + "\"";
        }

        if (elementId != null) {
            if (!label.equals("")) {
                label += " ";
            }
            label += "(id: " + elementId.trim() + ")";
        }

        if (label.isEmpty()) {
            label = "unknown " + elementType.trim();
        }
        return label;
    }

    private String getElementIdRepresentation(TBaseElement tBaseElement) {
        String elementId = getElementId(tBaseElement);
        return elementId != null ? elementId : "Process";
    }

    private String getElementId(TBaseElement tBaseElement) {
        if (tBaseElement != null && tBaseElement.getId() != null && !tBaseElement.getId().isEmpty()) {
            return tBaseElement.getId();
        } else {
            return null;
        }
    }

    private String getProcessName(TProcess tProcess) {
        if (tProcess != null && tProcess.getName() != null && !tProcess.getName().isEmpty()) {
            return tProcess.getName();
        } else {
            return null;
        }
    }

    private String getLaneName(TLane tLane) {
        if (tLane != null && tLane.getName() != null && !tLane.getName().isEmpty()) {
            return tLane.getName();
        } else {
            return null;
        }
    }

    private String getElementName(TFlowElement tFlowElement) {
        if (tFlowElement != null && tFlowElement.getName() != null && !tFlowElement.getName().isEmpty()) {
            return tFlowElement.getName();
        } else {
            return null;
        }
    }

}
