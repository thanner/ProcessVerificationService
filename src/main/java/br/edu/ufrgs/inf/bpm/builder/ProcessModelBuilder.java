package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.bpmn.*;
import br.edu.ufrgs.inf.bpm.changes.prom.BpmnProcessModelAdapter;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.ActivityType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.EventType;
import br.edu.ufrgs.inf.bpm.wrapper.elementType.GatewayType;
import org.processmining.framework.models.bpmn.*;
import org.processmining.mining.MiningResult;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessModelBuilder {

    private int genericId;
    private Map<String, BpmnSwimLane> laneMap;
    private Map<String, BpmnProcessModelAdapter> poolMap;
    private Map<String, BpmnElement> elementMap;
    private BpmnWrapper bpmnWrapper;
    private BpmnProcessModelAdapter bpmnProcessModel;

    public ProcessModelBuilder() {
        genericId = 0;
        laneMap = new HashMap<>();
        poolMap = new HashMap<>();
        elementMap = new HashMap<>();
    }

    public MiningResult buildProcess(TDefinitions definitions) {

        bpmnWrapper = new BpmnWrapper(definitions);

        int newId = generateModelId("ProcessModel1");
        bpmnProcessModel = new BpmnProcessModelAdapter("Process Model");
        BpmnGraph bpmnGraph = new BpmnGraph("Graph", bpmnProcessModel);

        List<TProcess> processList = bpmnWrapper.getProcessList();

        for (TProcess process : processList) {
            bpmnProcessModel = new BpmnProcessModelAdapter("Process Model Id");
            bpmnGraph = new BpmnGraph("Graph", bpmnProcessModel);
            bpmnProcessModel.addNode(new BpmnTask("1")); // Pegar do DOC?

            // bpmnGraph.addPool(createPool(process));
            /*
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    bpmnProcessModel.addLane(createLane(lane, process));
                }
            }
            */

            for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
                if (flowElement.getValue() instanceof TActivity) {
                    createActivity((TActivity) flowElement.getValue());
                } else if (flowElement.getValue() instanceof TEvent) {
                    createEvent((TEvent) flowElement.getValue());
                } else if (flowElement.getValue() instanceof TGateway) {
                    createGateway((TGateway) flowElement.getValue());
                }
            }

            for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
                if (flowElement.getValue() instanceof TSequenceFlow) {
                    createArc((TSequenceFlow) flowElement.getValue());
                }
            }

        }

        MiningResult miningResult = new BpmnResult(null, bpmnGraph);
        return miningResult;
    }

    /*
    private String createPool(TProcess process) {
        int newId = generateModelId(process.getId());
        Pool modelPool = new Pool(newId, processModelWrapper.getProcessName(process));
        poolMap.put(process.getId(), modelPool);
        return modelPool.getName();


        // Olha o que aconselha
        BpmnSwimPool pool = new BpmnSwimPool(element);
        pool.setType(BpmnSwimType.valueOf(tag));
        String poolid = pool.getId();
        pool.setpid(this.parentId);
        nodes.put(poolid, pool);
    }

    private String createLane(TLane lane, TProcess process) {
        int newId = generateModelId(lane.getId());
        Lane modelLane = new Lane(newId, getName(lane.getName()), poolMap.get(process.getId()));
        laneMap.put(lane.getId(), modelLane);
        return modelLane.getName();

        // Olha o que aconselha
        BpmnSwimLane lane = new BpmnSwimLane(element);
        lane.setType(BpmnSwimType.valueOf(tag));
        String laneid = lane.getId();
        lane.setpid(this.parentId);
        nodes.put(laneid, lane);
    }
    */

    private void createActivity(TActivity tActivity) {
        BpmnTask bpmnTask = new BpmnTask(tActivity.getId());

        bpmnTask.setName(tActivity.getName());
        bpmnTask.setLane(bpmnWrapper.getLaneByFlowNode(tActivity).getId());
        bpmnTask.setTypeTag(getActivityType(tActivity));
        bpmnTask.setpid(bpmnProcessModel.getParentId());

        bpmnProcessModel.putNode(bpmnTask.getId(), bpmnTask);

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

    private void createEvent(TEvent tEvent) {
        BpmnEvent bpmnEvent = new BpmnEvent(tEvent.getId());

        bpmnEvent.setpid(bpmnProcessModel.getParentId());
        bpmnEvent.setName(tEvent.getName());
        bpmnEvent.setLane(bpmnWrapper.getLaneByFlowNode(tEvent).getId());
        bpmnEvent.setTypeTag(getEventType(tEvent));

        bpmnProcessModel.putNode(bpmnEvent.getId(), bpmnEvent);
    }

    private void createGateway(TGateway tGateway) {
        BpmnGateway bpmnGateway = new BpmnGateway(tGateway.getId());

        bpmnGateway.setpid(bpmnProcessModel.getParentId());
        bpmnGateway.setName(tGateway.getName());
        bpmnGateway.setLane(bpmnWrapper.getLaneByFlowNode(tGateway).getId());
        bpmnGateway.setType(getGatewayType(tGateway));

        bpmnProcessModel.putNode(bpmnGateway.getId(), bpmnGateway);
    }

    private void createArc(TSequenceFlow tSequenceFlow) {
        Element element = JaxbWrapper.convertObjectToElement(tSequenceFlow);
        if (element != null) {
            BpmnEdge bpmnEdge = new BpmnEdge(element);
            bpmnEdge.setType(BpmnEdgeType.Flow);
            bpmnEdge.setpid(bpmnProcessModel.getParentId());

            bpmnProcessModel.putEdge(bpmnEdge.getId(), bpmnEdge);
        }
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

    /*
    private Pool getPoolByObject(TFlowNode flowNode) {
        TProcess process = processModelWrapper.getProcessByFlowNode(flowNode);
        return process != null ? poolMap.get(process.getId()) : null;
    }

    private Lane getLaneByObject(TFlowNode flowNode) {
        TLane lane = processModelWrapper.getLaneByFlowNode(flowNode);
        return lane != null ? laneMap.get(lane.getId()) : null;
    }

    */

    private int generateModelId(String oldId) {
        int newId = genericId++;
        return newId;
    }

}
