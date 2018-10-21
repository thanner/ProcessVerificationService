package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.verificationmessages.MessageType;
import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class ProcessModelVerification {

    List<TMessage> verificationMessages;
    BpmnWrapper bpmnWrapper;

    public List<TMessage> verify(TDefinitions tDefinitions) {
        verificationMessages = new ArrayList<>();
        bpmnWrapper = new BpmnWrapper(tDefinitions);

        for (TProcess tProcess : bpmnWrapper.getProcessList()) {
            verifyAmountStartEvents(tProcess);
            verifyAmountEndEvents(tProcess);
            verifyElementsWithoutLabels(tProcess);
            verifyCannonSplitJoin(tProcess);
        }

        verificationMessages.forEach(v -> v.setSource("Process Model Verification"));
        return verificationMessages;
    }

    private void verifyAmountStartEvents(TProcess tProcess) {
        List<TStartEvent> tStartEventList = bpmnWrapper.getFlowElementList(TStartEvent.class, tProcess);
        int size = tStartEventList.size();
        if (size == 0) {
            TMessage tMessage = new TMessage();
            tMessage.setProcessElementId("Process");
            tMessage.setDescription("No start event was identified in the process " + tProcess.getName() + ".");
            tMessage.setMessageType(MessageType.STRUCTURE);
            verificationMessages.add(tMessage);
        } else if (size > 1) {
            for (TStartEvent tStartEvent : tStartEventList) {
                TMessage tMessage = new TMessage();
                tMessage.setProcessElementId(tStartEvent.getId());
                tMessage.setDescription("Multiple start events were identified in the process " + tProcess.getName() + ".");
                tMessage.setMessageType(MessageType.STRUCTURE);
                verificationMessages.add(tMessage);
            }
        }
    }

    private void verifyAmountEndEvents(TProcess tProcess) {
        List<TEndEvent> tEndEventList = bpmnWrapper.getFlowElementList(TEndEvent.class, tProcess);

        TMessage tMessage;
        int size = tEndEventList.size();
        if (size == 0) {
            tMessage = new TMessage();
            tMessage.setProcessElementId("Process");
            tMessage.setDescription("No end event was identified in the process " + tProcess.getName() + ".");
            tMessage.setMessageType(MessageType.STRUCTURE);
            verificationMessages.add(tMessage);
        } else if (size > 1) {
            for (TEndEvent tEndEvent : tEndEventList) {
                tMessage = new TMessage();
                tMessage.setProcessElementId(tEndEvent.getId());
                tMessage.setDescription("Multiple end events were identified in the process " + tProcess.getName() + ".");
                tMessage.setMessageType(MessageType.STRUCTURE);
                verificationMessages.add(tMessage);
            }
        }
    }

    private void verifyElementsWithoutLabels(TProcess tProcess) {
        for (TLaneSet laneSet : tProcess.getLaneSet()) {
            for (TLane lane : laneSet.getLane()) {
                verifyElementWithoutLabels(lane.getId(), lane.getName(), "lane");
            }
        }

        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            if (flowElement.getValue() instanceof TActivity) {
                verifyElementWithoutLabels(flowElement.getValue(), "activity");
            } else if (flowElement.getValue() instanceof TEvent) {
                verifyElementWithoutLabels(flowElement.getValue(), "event");
            } else if (flowElement.getValue() instanceof TGateway) {
                verifyElementWithoutLabels(flowElement.getValue(), "gateway");
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
                verifyElementWithoutLabels(flowElement.getValue(), "boundary event");
            }
        }

    }

    private void verifyElementWithoutLabels(TFlowElement element, String elementType) {
        verifyElementWithoutLabels(element.getId(), element.getName(), elementType);
    }

    private void verifyElementWithoutLabels(String elementId, String elementName, String elementType) {
        if (elementName == null || elementName.isEmpty()) {
            TMessage tMessage = new TMessage();
            tMessage.setProcessElementId(elementId);
            tMessage.setDescription("No label was identified in the " + elementType + " " + elementId + ".");
            tMessage.setMessageType(MessageType.LABEL);
            verificationMessages.add(tMessage);
        }
    }

    // TODO: Fazer (Verificar se cada split leva a um join igual)
    private void verifyCannonSplitJoin(TProcess tProcess) {

    }

}
