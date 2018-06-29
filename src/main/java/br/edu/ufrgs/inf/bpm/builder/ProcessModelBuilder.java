package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.bpmn.*;
import br.edu.ufrgs.inf.bpm.changes.prom.BpmnProcessModelAdapter;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.ActivityType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.EventType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.GatewayType;
import org.processmining.framework.models.bpmn.*;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessModelBuilder {

    private List<BpmnEvent> eventList;
    private List<BpmnEdge> arcList;
    private BpmnWrapper bpmnWrapper;
    private BpmnProcessModelAdapter bpmnProcessModel;
    private Map<String, String> idMap;
    private Set<String> defaultPath;

    public BpmnResult buildProcess(TDefinitions definitions) {
        idMap = new HashMap<>();
        defaultPath = new HashSet<>();

        bpmnWrapper = new BpmnWrapper(definitions);
        BpmnGraph bpmnGraph = null;

        int id = 1;
        List<TProcess> processList = bpmnWrapper.getProcessList();
        for (TProcess tProcess : processList) {
            eventList = new ArrayList<>();
            arcList = new ArrayList<>();

            bpmnProcessModel = new BpmnProcessModelAdapter("Process Model " + id++);
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
                    createArc((TSequenceFlow) flowElement.getValue());
                }
            }

            setStartEvent();
            setEndEvent();

            bpmnGraph = new BpmnGraph("Graph", bpmnProcessModel);
        }

        return new BpmnResult(null, bpmnGraph);
    }

    public Map<String, String> getIdMap() {
        return idMap;
    }

    private void setStartEvent() {
        List<BpmnEvent> startEvents = eventList.stream().filter(e -> e.getTypeTag().equals(BpmnEventType.Start)).collect(Collectors.toList());
        BpmnEvent startEvent = null;
        switch (startEvents.size()) {
            case 0:
                // TODO: Devo criar um novo evento de inicio nesse caso ou devo deixar alertar?
                // startEvent = new BpmnEvent("Bpmn Start Event 1");
                break;
            case 1:
                startEvent = startEvents.get(0);
                break;
            default:
                startEvent = new BpmnEvent("Bpmn Start Event 1");
                startEvent.setTypeTag(BpmnEventType.Start);

                BpmnGateway bpmnGateway = new BpmnGateway("Bpmn Start XOR 1");
                bpmnGateway.setType(BpmnGatewayType.XOR);
                bpmnProcessModel.putNode(bpmnGateway.getId(), bpmnGateway);
                idMap.put(bpmnGateway.getNameAndId(), "");

                createEdge("Start - XOR", startEvent, bpmnGateway);
                for (BpmnEdge bpmnEdge : arcList) {
                    for (BpmnEvent event : startEvents) {
                        if (bpmnEdge.getFromId().equals(event.getId())) {
                            bpmnEdge.setFromId(startEvent.getId());
                        }
                    }
                }

                break;
        }

        if (startEvent != null) {
            bpmnProcessModel.setStart(startEvent);
            bpmnProcessModel.putNode(startEvent.getId(), startEvent);
            idMap.put(startEvent.getNameAndId(), "");
        }
    }

    private void setEndEvent() {
        List<BpmnEvent> endEvents = eventList.stream().filter(e -> e.getTypeTag().equals(BpmnEventType.End)).collect(Collectors.toList());
        BpmnEvent endEvent = null;
        switch (endEvents.size()) {
            case 0:
                // TODO: Devo criar um novo evento de inicio nesse caso ou devo deixar alertar?
                // startEvent = new BpmnEvent("Bpmn Start Event 1");
                break;
            case 1:
                endEvent = endEvents.get(0);
                break;
            default:
                endEvent = new BpmnEvent("Bpmn Start Event 1");
                endEvent.setTypeTag(BpmnEventType.End);

                BpmnGateway bpmnGateway = new BpmnGateway("Bpmn End XOR 1");
                bpmnGateway.setType(BpmnGatewayType.XOR);
                bpmnProcessModel.putNode(bpmnGateway.getId(), bpmnGateway);
                idMap.put(bpmnGateway.getNameAndId(), "");

                createEdge("End - XOR", bpmnGateway, endEvent);
                for (BpmnEdge bpmnEdge : arcList) {
                    for (BpmnEvent event : endEvents) {
                        if (bpmnEdge.getToId().equals(event.getId())) {
                            bpmnEdge.setToId(endEvent.getId());
                        }
                    }
                }

                break;
        }

        if (endEvent != null) {
            bpmnProcessModel.setEnd(endEvent);
            bpmnProcessModel.putNode(endEvent.getId(), endEvent);
            idMap.put(endEvent.getNameAndId(), "");
        }
    }

    private void createPool(TProcess tProcess) {
        Element element = JaxbWrapper.convertObjectToElement(tProcess);
        if (element != null) {
            BpmnSwimPool pool = new BpmnSwimPool(element);

            pool.setName(tProcess.getName());
            pool.setType(BpmnSwimType.Pool);
            pool.setpid(bpmnProcessModel.getParentId());

            bpmnProcessModel.putNode(pool.getId(), pool);
            idMap.put(pool.getId(), tProcess.getId());
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
            idMap.put(bpmnSwimLane.getId(), tLane.getId());
        }
    }

    private void createActivity(TActivity tActivity) {
        BpmnTask bpmnTask = new BpmnTask(tActivity.getId());

        bpmnTask.setName(tActivity.getName());
        bpmnTask.setLane(getLaneId(tActivity));
        bpmnTask.setTypeTag(getActivityType(tActivity));
        bpmnTask.setpid(bpmnProcessModel.getParentId());

        bpmnProcessModel.putNode(bpmnTask.getId(), bpmnTask);
        idMap.put(bpmnTask.getNameAndId(), tActivity.getId());

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

        if (bpmnEvent.getTypeTag().equals(BpmnEventType.Intermediate)) {
            bpmnProcessModel.putNode(bpmnEvent.getId(), bpmnEvent);
            idMap.put(bpmnEvent.getNameAndId(), tEvent.getId());
        }
    }

    private void createGateway(TGateway tGateway) {
        BpmnGateway bpmnGateway = new BpmnGateway(tGateway.getId());

        bpmnGateway.setpid(bpmnProcessModel.getParentId());
        bpmnGateway.setName(tGateway.getName());
        bpmnGateway.setLane(getLaneId(tGateway));
        bpmnGateway.setType(getGatewayType(tGateway));
        setPathDefault(bpmnGateway, tGateway);

        bpmnProcessModel.putNode(bpmnGateway.getId(), bpmnGateway);
        idMap.put(bpmnGateway.getNameAndId(), tGateway.getId());
    }

    private void setPathDefault(BpmnGateway bpmnGateway, TGateway tGateway) {
        TSequenceFlow path = null;

        if (tGateway instanceof TExclusiveGateway) {
            path = (TSequenceFlow) ((TExclusiveGateway) tGateway).getDefault();
        } else if (tGateway instanceof TInclusiveGateway) {
            path = (TSequenceFlow) ((TInclusiveGateway) tGateway).getDefault();
        }

        if (path != null) {
            defaultPath.add(path.getId());
        }
    }

    private String getLaneId(TFlowNode node) {
        TLane lane = bpmnWrapper.getLaneByFlowNode(node);
        return lane != null ? lane.getId() : "";
    }

    private void createArc(TSequenceFlow tSequenceFlow) {
        Element element = JaxbWrapper.convertObjectToElement(tSequenceFlow);
        if (element != null) {
            BpmnEdge bpmnEdge = new BpmnEdge(element);
            bpmnEdge.setType(BpmnEdgeType.Flow);
            bpmnEdge.setpid(bpmnProcessModel.getParentId());

            bpmnEdge.setDefaultFlag(defaultPath.contains(tSequenceFlow.getId()));

            bpmnEdge.setFromId(((TFlowElement) tSequenceFlow.getSourceRef()).getId());
            bpmnEdge.setToId(((TFlowElement) tSequenceFlow.getTargetRef()).getId());

            arcList.add(bpmnEdge);

            bpmnProcessModel.putEdge(bpmnEdge.getId(), bpmnEdge);
            idMap.put(bpmnEdge.getFromId() + " -> " + bpmnEdge.getToId(), bpmnEdge.getId());
        }
    }

    // FIXME: New BPMNEdge exige um element
    private void createEdge(String edgeId, BpmnElement from, BpmnElement to) {
        BpmnEdge bpmnEdge = new BpmnEdge(null);

        bpmnEdge.setType(BpmnEdgeType.Flow);
        bpmnEdge.setpid(bpmnProcessModel.getParentId());
        bpmnEdge.setFromId(from.getId());
        bpmnEdge.setToId(to.getId());
        bpmnProcessModel.putEdge(bpmnEdge.getId(), bpmnEdge);
        idMap.put(bpmnEdge.getFromId() + " -> " + bpmnEdge.getToId(), bpmnEdge.getId());
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

    // FIXME: NÃO SEI
    private IllegalArgumentException getIllegalTypeException(TFlowNode flowNode) {
        return new IllegalArgumentException("Can not find element type (Element: " + flowNode.getClass().getSimpleName() + ". Id: " + flowNode.getId() + ")");
    }

}
