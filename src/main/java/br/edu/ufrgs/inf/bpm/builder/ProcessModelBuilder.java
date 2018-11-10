package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.changes.prom.BpmnProcessModelAdapter;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.ActivityType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.EventType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.GatewayType;
import org.omg.spec.bpmn._20100524.model.*;
import org.processmining.framework.models.bpmn.*;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessModelBuilder {

    private List<BpmnEvent> eventList;
    private List<BpmnEdge> sequenceFLowList;

    private List<TStartEvent> startEventList;
    private List<TEndEvent> endEventList;

    private BpmnWrapper bpmnWrapper;
    private BpmnProcessModelAdapter bpmnProcessModel;
    private Map<String, TBaseElement> bpmnResultBpmnMap;
    private Set<String> defaultPath;
    private Set<String> predicatePath;

    public List<BpmnResult> buildProcess(TDefinitions definitions) {
        bpmnResultBpmnMap = new HashMap<>();
        defaultPath = new HashSet<>();
        predicatePath = new HashSet<>();
        bpmnWrapper = new BpmnWrapper(definitions);

        List<BpmnResult> bpmnResultList = new ArrayList<>();
        int processId = 1;

        List<TProcess> processList = bpmnWrapper.getProcessList();
        for (TProcess tProcess : processList) {
            eventList = new ArrayList<>();
            sequenceFLowList = new ArrayList<>();

            startEventList = new ArrayList<>();
            endEventList = new ArrayList<>();

            bpmnProcessModel = new BpmnProcessModelAdapter("Process Model " + processId);
            createPool(tProcess);

            for (TLaneSet laneSet : tProcess.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    createLane(lane);
                }
            }

            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue() instanceof TActivity) {
                    createActivity((TActivity) flowElement.getValue());
                } else if (flowElement.getValue() instanceof TEvent) {
                    prepareEvent((TEvent) flowElement.getValue());
                } else if (flowElement.getValue() instanceof TGateway) {
                    createGateway((TGateway) flowElement.getValue());
                }
            }

            for (JAXBElement<? extends TFlowElement> flowElement : tProcess.getFlowElement()) {
                if (flowElement.getValue() instanceof TSequenceFlow) {
                    createSequenceFlow((TSequenceFlow) flowElement.getValue());
                }
            }

            setStartEvent();
            setEndEvent();

            BpmnGraph bpmnGraph = new BpmnGraph("Graph " + processId, bpmnProcessModel);
            if (!bpmnWrapper.isBlackBox(tProcess)) {
                bpmnResultList.add(new BpmnResult(null, bpmnGraph));
            }
            processId++;
        }

        return bpmnResultList;
    }

    public Map<String, TBaseElement> getBpmnResultBpmnMap() {
        return bpmnResultBpmnMap;
    }

    private void setStartEvent() {
        List<BpmnEvent> startEvents = eventList.stream().filter(e -> e.getTypeTag().equals(BpmnEventType.Start)).collect(Collectors.toList());

        switch (startEvents.size()) {
            case 0:
                break;
            case 1:
                BpmnEvent startEvent = startEvents.get(0);
                bpmnProcessModel.setStart(startEvent);
                bpmnProcessModel.putNode(startEvent.getId(), startEvent);
                break;
            default:
                for (BpmnEvent bpmnEvent : startEvents) {
                    bpmnProcessModel.putNode(bpmnEvent.getId(), bpmnEvent);
                }
                break;
        }

        /*
        switch (startEvents.size()) {
            case 0:
                break;
            case 1:
                startEvent = startEvents.get(0);
                tStartEvent = startEventList.get(0);
                break;
            default:
                startEvent = new BpmnEvent("Bpmn Start Event 1");
                startEvent.setTypeTag(BpmnEventType.Start);
                startEvent.setpid(bpmnProcessModel.getParentId());

                tStartEvent.setId(startEvent.getId());

                TExclusiveGateway tExclusiveGateway = new TExclusiveGateway();
                tExclusiveGateway.setId("Exclusive XOR Start");
                createGateway(tExclusiveGateway);

                createSequenceFlow("Start - XOR", tStartEvent, tExclusiveGateway);

                for (BpmnEdge bpmnEdge : sequenceFLowList) {
                    for (BpmnEvent event : startEvents) {
                        if (bpmnEdge.getFromId().equals(event.getId())) {
                            bpmnEdge.setFromId(tStartEvent.getId());
                        }
                    }
                }

                break;
        }

        if (startEvent != null) {
            bpmnProcessModel.setStart(startEvent);
            bpmnProcessModel.putNode(startEvent.getId(), startEvent);
            bpmnResultBpmnMap.put(startEvent.getNameAndId(), tStartEvent);
        }
        */
    }

    private void setEndEvent() {
        List<BpmnEvent> endEvents = eventList.stream().filter(e -> e.getTypeTag().equals(BpmnEventType.End)).collect(Collectors.toList());

        switch (endEvents.size()) {
            case 0:
                break;
            case 1:
                BpmnEvent endEvent = endEvents.get(0);
                bpmnProcessModel.setEnd(endEvent);
                bpmnProcessModel.putNode(endEvent.getId(), endEvent);
                break;
            default:
                for (BpmnEvent bpmnEvent : endEvents) {
                    bpmnProcessModel.putNode(bpmnEvent.getId(), bpmnEvent);
                }
                break;
        }

        /*
        BpmnEvent endEvent = null;
        TEndEvent tEndEvent = new TEndEvent();

        switch (endEvents.size()) {
            case 0:
                break;
            case 1:
                endEvent = endEvents.get(0);
                tEndEvent = endEventList.get(0);
                break;
            default:
                endEvent = new BpmnEvent("Bpmn End Event 1");
                endEvent.setTypeTag(BpmnEventType.End);
                endEvent.setpid(bpmnProcessModel.getParentId());

                tEndEvent.setId(endEvent.getId());

                TExclusiveGateway tExclusiveGateway = new TExclusiveGateway();
                tExclusiveGateway.setId("Exclusive XOR End");
                createGateway(tExclusiveGateway);

                createSequenceFlow("End - XOR", tExclusiveGateway, tEndEvent);

                for (BpmnEdge bpmnEdge : sequenceFLowList) {
                    for (BpmnEvent event : endEvents) {
                        if (bpmnEdge.getToId().equals(event.getId())) {
                            bpmnEdge.setToId(tEndEvent.getId());
                        }
                    }
                }

                break;
        }

        if (endEvent != null) {
            bpmnProcessModel.setEnd(endEvent);
            bpmnProcessModel.putNode(endEvent.getId(), endEvent);
            bpmnResultBpmnMap.put(endEvent.getNameAndId(), tEndEvent);
        }
        */
    }

    private void createPool(TProcess tProcess) {
        Element element = JaxbWrapper.convertObjectToElement(tProcess);
        if (element != null) {
            BpmnSwimPool pool = new BpmnSwimPool(element);

            pool.setName(tProcess.getName());
            pool.setType(BpmnSwimType.Pool);
            pool.setpid(bpmnProcessModel.getParentId());

            bpmnProcessModel.putNode(pool.getId(), pool);
            bpmnResultBpmnMap.put(pool.getId(), tProcess);
        }
    }

    private void createLane(TLane tLane) {
        Element element = JaxbWrapper.convertObjectToElement(tLane);
        if (element != null) {
            BpmnSwimLane bpmnSwimLane = new BpmnSwimLane(element);

            bpmnSwimLane.setName(tLane.getName());
            bpmnSwimLane.setType(BpmnSwimType.Lane);
            bpmnSwimLane.setpid(bpmnProcessModel.getParentId());

            bpmnProcessModel.putNode(bpmnSwimLane.getId(), bpmnSwimLane);
            bpmnResultBpmnMap.put(bpmnSwimLane.getId(), tLane);
        }
    }

    private void createActivity(TActivity tActivity) {
        BpmnTask bpmnTask = new BpmnTask(tActivity.getId());

        bpmnTask.setName(tActivity.getName());
        bpmnTask.setLane(getLaneId(tActivity));
        bpmnTask.setTypeTag(getActivityType(tActivity));
        bpmnTask.setpid(bpmnProcessModel.getParentId());

        bpmnProcessModel.putNode(bpmnTask.getId(), bpmnTask);
        bpmnResultBpmnMap.put(bpmnTask.getNameAndId(), tActivity);

        /**
         //handle its child of intermediate event type
         NodeList childNodes = element.getElementsByTagName(BpmnXmlTags.BPMN_INTERMEDIATE);
         int childrenNum = childNodes.getLength();
         for (int i = 0; i < childrenNum; i++)
         {
         Node nd = childNodes.item(i);
         if (nd instanceof Element)
         {
         BpmnEvent event = (BpmnEvent)parseElement((Element) nd);
         event.setLane(task.getLane());
         if(event != null)
         {
         String edgeId = event.getId() + "_" + id;
         BpmnEdge edge = new BpmnEdge(event.getId(), id);
         edge.setType(BpmnEdgeType.Flow);
         // set the parent id
         edge.setpid(bpmnProcessModel.getParentId());
         edge.setId(edgeId);
         // save the node
         bpmnProcessModel.putEdge(edgeId, edge);
         }
         }
         }
         **/
    }

    private void prepareEvent(TEvent tEvent) {
        BpmnEvent bpmnEvent = new BpmnEvent(tEvent.getId());

        bpmnEvent.setpid(bpmnProcessModel.getParentId());
        bpmnEvent.setName(tEvent.getName());
        bpmnEvent.setLane(getLaneId(tEvent));

        bpmnEvent.setTypeTag(getEventType(tEvent));

        eventList.add(bpmnEvent);
        bpmnResultBpmnMap.put(bpmnEvent.getNameAndId(), tEvent);

        if (bpmnEvent.getTypeTag().equals(BpmnEventType.Intermediate)) {
            bpmnProcessModel.putNode(bpmnEvent.getId(), bpmnEvent);
        }

        if (tEvent instanceof TStartEvent) {
            startEventList.add((TStartEvent) tEvent);
        } else if (tEvent instanceof TEndEvent) {
            endEventList.add((TEndEvent) tEvent);
        }
    }

    private void createGateway(TGateway tGateway) {
        BpmnGateway bpmnGateway = new BpmnGateway(tGateway.getId());

        bpmnGateway.setpid(bpmnProcessModel.getParentId());
        bpmnGateway.setName(tGateway.getName());
        bpmnGateway.setLane(getLaneId(tGateway));
        bpmnGateway.setType(getGatewayType(tGateway));
        setPathDefault(tGateway);

        bpmnProcessModel.putNode(bpmnGateway.getId(), bpmnGateway);
        bpmnResultBpmnMap.put(bpmnGateway.getNameAndId(), tGateway);
    }

    private void setPathDefault(TGateway tGateway) {
        boolean needDefault = true;
        TSequenceFlow path = null;

        if (tGateway instanceof TExclusiveGateway && tGateway.getOutgoing().size() > 1) {
            path = (TSequenceFlow) ((TExclusiveGateway) tGateway).getDefault();
        } else if (tGateway instanceof TInclusiveGateway && tGateway.getOutgoing().size() > 1) {
            path = (TSequenceFlow) ((TInclusiveGateway) tGateway).getDefault();
        } else {
            needDefault = false;
        }

        List<TSequenceFlow> targetSequenceFlowList = getTargetSequenceFlow(tGateway);

        if (needDefault && targetSequenceFlowList.size() > 0) {
            if (path == null) {
                path = targetSequenceFlowList.get(0);
            }

            defaultPath.add(path.getId());
            final String pathId = path.getId();
            Set<String> predicateList = targetSequenceFlowList.stream().filter(s -> !s.getId().equals(pathId)).map(TBaseElement::getId).collect(Collectors.toSet());
            predicatePath.addAll(predicateList);
        }
    }

    private List<TSequenceFlow> getTargetSequenceFlow(TGateway tGateway) {
        List<TSequenceFlow> targetSequenceFlow = new ArrayList<>();
        for (QName qName : tGateway.getOutgoing()) {
            TFlowElement tFlowElement = bpmnWrapper.getFlowElementByQName(qName);
            if (tFlowElement instanceof TSequenceFlow) {
                targetSequenceFlow.add((TSequenceFlow) tFlowElement);
            }
        }
        return targetSequenceFlow;
    }

    private String getLaneId(TFlowNode node) {
        TLane lane = bpmnWrapper.getLaneByFlowNode(node);
        return lane != null ? lane.getId() : "";
    }

    private void createSequenceFlow(TSequenceFlow tSequenceFlow) {
        Element element = JaxbWrapper.convertObjectToElement(tSequenceFlow);
        if (element != null) {

            BpmnEdge bpmnEdge = new BpmnEdge(element);
            bpmnEdge.setType(BpmnEdgeType.Flow);
            bpmnEdge.setpid(bpmnProcessModel.getParentId());

            boolean hasDefault = defaultPath.contains(tSequenceFlow.getId());
            if (hasDefault) {
                bpmnEdge.setDefaultFlag(true);
            }

            boolean hasPredicate = predicatePath.contains(tSequenceFlow.getId());
            if (hasPredicate) {
                bpmnEdge.setCondition("true()");
            }

            TFlowElement tFlowElementFrom = (TFlowElement) tSequenceFlow.getSourceRef();
            bpmnEdge.setFromId(tFlowElementFrom.getId());
            String nameFrom = tFlowElementFrom.getName() != null ? tFlowElementFrom.getName() : "";
            String fromIdentifier = nameFrom + "\t" + tFlowElementFrom.getId();

            TFlowElement tFlowElementTo = (TFlowElement) tSequenceFlow.getTargetRef();
            bpmnEdge.setToId(((TFlowElement) tSequenceFlow.getTargetRef()).getId());
            String nameTo = tFlowElementTo.getName() != null ? tFlowElementTo.getName() : "";
            String toIdentifier = nameTo + "\t" + tFlowElementTo.getId();

            sequenceFLowList.add(bpmnEdge);

            bpmnProcessModel.putEdge(bpmnEdge.getId(), bpmnEdge);
            bpmnResultBpmnMap.put(fromIdentifier + " -> " + toIdentifier, tSequenceFlow);
        }
    }

    private void createSequenceFlow(String edgeId, TFlowElement fromElement, TFlowElement toElement) {
        TSequenceFlow tSequenceFlow = new TSequenceFlow();
        tSequenceFlow.setId(edgeId);

        tSequenceFlow.setSourceRef(fromElement);
        tSequenceFlow.setTargetRef(toElement);

        createSequenceFlow(tSequenceFlow);
    }

    private BpmnTaskType getActivityType(TActivity tActivity) throws IllegalArgumentException {
        try {
            return ActivityType.valueOf(tActivity.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(tActivity);
        }
    }

    private BpmnEventType getEventType(TEvent tEvent) throws IllegalArgumentException {
        try {
            return EventType.valueOf(tEvent.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(tEvent);
        }
    }

    private BpmnGatewayType getGatewayType(TGateway gateway) throws IllegalArgumentException {
        try {
            return GatewayType.valueOf(gateway.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(gateway);
        }
    }

    private IllegalArgumentException getIllegalTypeException(TFlowNode flowNode) {
        return new IllegalArgumentException("Can not find element type (Element: " + flowNode.getClass().getSimpleName() + ". Id: " + flowNode.getId() + ")");
    }

}
