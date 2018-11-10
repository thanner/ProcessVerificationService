package br.edu.ufrgs.inf.bpm.wrapper;

import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BpmnWrapper {

    private TDefinitions definitions;

    public BpmnWrapper(TDefinitions definitions) {
        this.definitions = definitions;
    }

    public TProcess getProcessByFlowNode(TFlowNode flowNode) {
        List<TProcess> processList = getProcessList();
        for (TProcess process : processList) {
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    for (JAXBElement<Object> flowNodeRefObject : lane.getFlowNodeRef()) {
                        if (flowNodeRefObject.getValue() instanceof TFlowNode) {
                            TFlowNode flowNodeAux = (TFlowNode) flowNodeRefObject.getValue();
                            if (flowNode.getId().equals(flowNodeAux.getId())) {
                                return process;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public TLane getLaneByFlowNode(TFlowNode flowNode) {
        List<TProcess> processList = getProcessList();
        for (TProcess process : processList) {
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    for (JAXBElement<Object> flowNodeRefObject : lane.getFlowNodeRef()) {
                        if (flowNodeRefObject.getValue() instanceof TFlowNode) {
                            TFlowNode flowNodeAux = (TFlowNode) flowNodeRefObject.getValue();
                            if (flowNode.getId().equals(flowNodeAux.getId())) {
                                return lane;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public <T> List<T> getFlowElementList(Class<T> flowElementClass, TProcess tProcess) {
        List<T> elementList = new ArrayList<>();
        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            if (flowElementClass.isInstance(flowElement.getValue())) {
                elementList.add((T) flowElement.getValue());
            }
        }
        return elementList;
    }

    public TLane getLaneByFlowElement(TFlowElement tFlowElement) {
        List<TProcess> processList = getProcessList();
        for (TProcess process : processList) {
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    for (JAXBElement<Object> flowNodeRefObject : lane.getFlowNodeRef()) {
                        if (flowNodeRefObject.getValue() instanceof TFlowNode) {
                            TFlowNode flowNodeAux = (TFlowNode) flowNodeRefObject.getValue();
                            if (tFlowElement.getId().equals(flowNodeAux.getId())) {
                                return lane;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<TCollaboration> getCollaborationList() {
        List<TCollaboration> collaborationList = new ArrayList<>();
        List<JAXBElement<? extends TRootElement>> rootElementList = definitions.getRootElement();
        for (JAXBElement<? extends TRootElement> rootElement : rootElementList) {
            if (rootElement.getValue() instanceof TCollaboration) {
                TCollaboration collaboration = (TCollaboration) rootElement.getValue();
                collaborationList.add(collaboration);
            }
        }
        return collaborationList;
    }

    public List<TProcess> getProcessList() {
        List<TProcess> processList = new ArrayList<>();

        List<JAXBElement<? extends TRootElement>> rootElementList = definitions.getRootElement();
        for (JAXBElement<? extends TRootElement> root : rootElementList) {
            if (root.getValue() instanceof TProcess) {
                TProcess process = (TProcess) root.getValue();
                processList.add(process);
            }
        }
        return processList;
    }

    public TFlowElement getFlowElementByQName(QName qName) {
        return getFlowElementById(qName.getLocalPart());
    }

    public TFlowElement getFlowElementById(String flowElementId) {
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue().getId().equals(flowElementId)) {
                    return flowElement.getValue();
                }
            }
        }
        return null;
    }

    public String getProcessName(TProcess process) {
        List<TCollaboration> collaborationList = getCollaborationList();
        for (TCollaboration collaboration : collaborationList) {
            for (TParticipant participant : collaboration.getParticipant()) {
                if (process.getId().equals(participant.getProcessRef().toString())) {
                    return participant.getName();
                }
            }
        }
        return "";
    }

    public TParticipant getParticipantFromProcess(TProcess tProcess) {
        List<TCollaboration> collaborationList = getCollaborationList();
        for (TCollaboration collaboration : collaborationList) {
            for (TParticipant participant : collaboration.getParticipant()) {
                if (tProcess.getId().equals(participant.getProcessRef().toString())) {
                    return participant;
                }
            }
        }
        return null;
    }

    public boolean hasParticipant(TProcess process) {
        List<TCollaboration> collaborationList = getCollaborationList();
        for (TCollaboration collaboration : collaborationList) {
            for (TParticipant participant : collaboration.getParticipant()) {
                if (process.getId().equals(participant.getProcessRef().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<TSequenceFlow> getSequenceFlowList() {
        List<TSequenceFlow> tSequenceFlowList = new ArrayList<>();
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue() instanceof TSequenceFlow) {
                    tSequenceFlowList.add((TSequenceFlow) flowElement.getValue());
                }
            }
        }
        return tSequenceFlowList;
    }

    public List<TActivity> getActivityList() {
        List<TActivity> tActivityList = new ArrayList<>();
        for (TProcess tProcess : getProcessList()) {
            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue() instanceof TActivity) {
                    tActivityList.add((TActivity) flowElement.getValue());
                }
            }
        }
        return tActivityList;
    }

    public List<TSequenceFlow> getTargetSequenceFlows(TFlowNode tFlowNode) {
        List<TSequenceFlow> sequenceFlowList = new ArrayList<>();
        for (QName qName : tFlowNode.getOutgoing()) {
            TFlowElement tFlowElement = getFlowElementByQName(qName);
            if (tFlowElement instanceof TSequenceFlow) {
                sequenceFlowList.add((TSequenceFlow) tFlowElement);
            }
        }
        return sequenceFlowList;
    }

    public List<TFlowNode> getFlowNodesWithoutIncoming(TProcess tProcess) {
        List<TFlowNode> flowNodeWithoutIncomingList = new ArrayList();
        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            TFlowElement tFlowElement = flowElement.getValue();
            if (tFlowElement instanceof TFlowNode) {
                TFlowNode tFlowNode = (TFlowNode) tFlowElement;
                if (tFlowNode.getIncoming() == null || tFlowNode.getIncoming().isEmpty()) {
                    flowNodeWithoutIncomingList.add(tFlowNode);
                }
            }
        }

        return flowNodeWithoutIncomingList;
    }

    public List<TFlowNode> getFlowNodesWithoutOutgoing(TProcess tProcess) {
        List<TFlowNode> flowNodeWithoutOutgoingList = new ArrayList();
        for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
            TFlowElement tFlowElement = flowElement.getValue();
            if (tFlowElement instanceof TFlowNode) {
                TFlowNode tFlowNode = (TFlowNode) tFlowElement;
                if (tFlowNode.getOutgoing() == null || tFlowNode.getOutgoing().isEmpty()) {
                    flowNodeWithoutOutgoingList.add(tFlowNode);
                }
            }
        }

        return flowNodeWithoutOutgoingList;
    }

    public int getAmountFlowNode(TProcess tProcess) {
        return tProcess.getFlowElement().stream().filter(e -> e.getValue() instanceof TFlowNode).collect(Collectors.toList()).size();
    }

    public boolean isBlackBox(TProcess tProcess) {
        return tProcess.getFlowElement().isEmpty();
    }

}