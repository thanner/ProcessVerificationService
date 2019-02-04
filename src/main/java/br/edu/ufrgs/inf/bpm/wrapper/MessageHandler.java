package br.edu.ufrgs.inf.bpm.wrapper;

import org.omg.spec.bpmn._20100524.model.*;

public class MessageHandler {

    public String getElementType(TFlowElement tFlowElement) {
        String elementType = "Process Element";
        if (tFlowElement instanceof TActivity) {
            elementType = "Activity";
        } else if (tFlowElement instanceof TEvent) {
            elementType = getEventType((TEvent) tFlowElement);
        } else if (tFlowElement instanceof TGateway) {
            elementType = getGatewayType((TGateway) tFlowElement);
        }

        return elementType;
    }

    public String handleActivityError(TActivity tActivity) {
        String elementDescription = "Activity" + getFlowNodeData(tActivity);
        return elementDescription;
    }

    public String handleGatewayError(TGateway tGateway) {
        String elementDescription = getGatewayType(tGateway) + getFlowNodeData(tGateway);
        return elementDescription;
    }

    public String handleEventError(TEvent tEvent) {
        String elementDescription = getEventType(tEvent) + getFlowNodeData(tEvent);
        return elementDescription;
    }

    public String handleSequenceFlowError(TSequenceFlow tSequenceFlow) {
        String elementDescription = "Sequence Flow" + getSequenceFlowData(tSequenceFlow);
        return elementDescription;
    }

    public String handleFlowNodeError(TFlowNode flowNode) {
        return "Process Element" + getFlowNodeData(flowNode);
    }

    public String handleBaseElementError(TBaseElement tBaseElement) {
        return "Process Element" + getBaseElementData(tBaseElement);
    }

    public String getGatewayType(TGateway tGateway) {
        if (tGateway instanceof TExclusiveGateway) {
            return "Exclusive Gateway";
        } else if (tGateway instanceof TInclusiveGateway) {
            return "Inclusive Gateway";
        } else if (tGateway instanceof TParallelGateway) {
            return "Parallel Gateway";
        } else if (tGateway instanceof TEventBasedGateway) {
            return "Event Based Gateway";
        } else {
            return "Gateway";
        }
    }

    public String getEventType(TEvent tEvent) {
        if (tEvent instanceof TStartEvent) {
            return "Start Event";
        } else if (tEvent instanceof TIntermediateCatchEvent) {
            return "Intermediate Catch Event";
        } else if (tEvent instanceof TIntermediateThrowEvent) {
            return "Intermediate Throw Event";
        } else if (tEvent instanceof TEndEvent) {
            return "End Event";
        } else {
            return "Event";
        }
    }

    private String getFlowNodeData(TFlowNode tFlowNode) {
        String elementDescription = "";
        if (tFlowNode.getName() != null && !tFlowNode.getName().isEmpty()) {
            elementDescription = ": " + tFlowNode.getName().trim();
        }
        if (tFlowNode.getId() != null && !tFlowNode.getId().isEmpty()) {
            elementDescription += " (id: " + tFlowNode.getId().trim() + ")";
        }

        return elementDescription;
    }

    private String getSequenceFlowData(TSequenceFlow tSequenceFlow) {
        String elementDescription = "";
        if (tSequenceFlow.getName() != null && !tSequenceFlow.getName().isEmpty()) {
            elementDescription = ": " + tSequenceFlow.getName().trim();
        }
        if (tSequenceFlow.getId() != null && !tSequenceFlow.getId().isEmpty()) {
            elementDescription += " (id: " + tSequenceFlow.getId().trim() + ")";
        }

        return elementDescription;
    }

    private String getBaseElementData(TBaseElement tBaseElement) {
        String elementDescription = "";
        if (tBaseElement.getId() != null && !tBaseElement.getId().isEmpty()) {
            elementDescription += " (id: " + tBaseElement.getId().trim() + ")";
        }

        return elementDescription;
    }

}
