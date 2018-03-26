package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.bpmn.TActivity;
import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.bpmn.TProcess;
import br.edu.ufrgs.inf.bpm.changes.prom.BpmnProcessModelAdapter;
import br.edu.ufrgs.inf.bpm.wrapper.BpmnWrapper;
import org.processmining.framework.models.bpmn.BpmnElement;
import org.processmining.framework.models.bpmn.BpmnGraph;
import org.processmining.framework.models.bpmn.BpmnSwimLane;
import org.processmining.framework.models.bpmn.BpmnTask;
import org.processmining.mining.MiningResult;
import org.processmining.mining.bpmnmining.BpmnResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessModelBuilder {

    private int genericId;
    private Map<String, BpmnSwimLane> laneMap;
    private Map<String, BpmnProcessModelAdapter> poolMap;
    private Map<String, BpmnElement> elementMap;
    private BpmnWrapper processModelWrapper;

    public ProcessModelBuilder() {
        genericId = 0;
        laneMap = new HashMap<>();
        poolMap = new HashMap<>();
        elementMap = new HashMap<>();
    }

    public MiningResult buildProcess(TDefinitions definitions) {

        processModelWrapper = new BpmnWrapper(definitions);

        int newId = generateModelId("ProcessModel1");
        BpmnProcessModelAdapter processModel = new BpmnProcessModelAdapter("Process Model");
        BpmnGraph bpmnGraph = new BpmnGraph("Graph", processModel);

        List<TProcess> processList = processModelWrapper.getProcessList();

        for (TProcess process : processList) {
            processModel = new BpmnProcessModelAdapter("Process Model");
            bpmnGraph = new BpmnGraph("Graph", processModel);

            processModel.addNode(new BpmnTask("1")); // Pegar do DOC?

            //bpmnGraph.addPool(createPool(process));
            /*
            for (TLaneSet laneSet : process.getLaneSet()) {
                for (TLane lane : laneSet.getLane()) {
                    processModel.addLane(createLane(lane, process));
                }
            }
            */

            /*
            for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
                if (flowElement.getValue() instanceof TActivity) {
                    processModel.addActivity(createActivity((TActivity) flowElement.getValue()));
                } else if (flowElement.getValue() instanceof TEvent) {
                    processModel.addEvent(createEvent((TEvent) flowElement.getValue()));
                } else if (flowElement.getValue() instanceof TGateway) {
                    processModel.addGateway(createGateway((TGateway) flowElement.getValue()));
                }
            }

            for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
                if (flowElement.getValue() instanceof TSequenceFlow) {
                    processModel.addArc(createArc((TSequenceFlow) flowElement.getValue()));
                }
            }
            */

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
    }

    private String createLane(TLane lane, TProcess process) {
        int newId = generateModelId(lane.getId());
        Lane modelLane = new Lane(newId, getName(lane.getName()), poolMap.get(process.getId()));
        laneMap.put(lane.getId(), modelLane);
        return modelLane.getName();
    }
    */

    private BpmnTask createTask(TActivity activity) {
        /*
        // Task and activities starting with the tag "Task"
        BpmnTask task = new BpmnTask(element);
        String id = task.getId();
        // set the type by the starting tag name
        task.setTypeTag(BpmnTaskType.valueOf(tag));
        // set the parent id
        task.setpid(this.parentId);
        // save the node
        nodes.put(id, task);

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
                    edge.setpid(this.parentId);
                    edge.setId(edgeId);
                    // save the node
                    edges.put(edgeId, edge);
                }
            }
        }
        return task;
        */
        return null;
    }

    /*
    private BpmnEvent createEvent(TEvent event) {
        try {
            int newId = generateModelId(event.getId());
            int eventType = getEventType(event);
            Event modelEvent = new Event(newId, getName(event.getName()), getLaneByObject(event), getPoolByObject(event), eventType);
            elementMap.put(event.getId(), modelEvent);
            return modelEvent;
        } catch (IllegalArgumentException i) {
            i.printStackTrace();
        }
        return null;
    }

    private BpmnGateway createGateway(TGateway gateway) {
        try {
            int newId = generateModelId(gateway.getId());
            int gatewayType = getGatewayType(gateway);
            Gateway modelGateway = new Gateway(newId, getName(gateway.getName()), getLaneByObject(gateway), getPoolByObject(gateway), gatewayType);
            elementMap.put(gateway.getId(), modelGateway);
            return modelGateway;
        } catch (IllegalArgumentException i) {
            i.printStackTrace();
        }
        return null;
    }

    private BPMNEdge createArc(TSequenceFlow arc) {
        int newId = generateModelId(arc.getId());
        return new Arc(newId, getName(arc.getName()), elementMap.get(((TFlowNode) arc.getSourceRef()).getId()), elementMap.get(((TFlowNode) arc.getTargetRef()).getId()));
    }

    private String getName(String name) {
        return name != null ? name : "";
    }

    private int getActivityType(TActivity activity) throws IllegalArgumentException {
        try {
            return ActivityType.valueOf(activity.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(activity);
        }
    }

    private int getEventType(TEvent event) throws IllegalArgumentException {
        try {
            return EventType.valueOf(event.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(event);
        }
    }

    private int getGatewayType(TGateway gateway) throws IllegalArgumentException {
        try {
            return GatewayType.valueOf(gateway.getClass().getSimpleName()).getValue();
        } catch (IllegalArgumentException e) {
            throw getIllegalTypeException(gateway);
        }
    }

    private IllegalArgumentException getIllegalTypeException(TFlowNode flowNode) {
        return new IllegalArgumentException("Can not find element type (Element: " + flowNode.getClass().getSimpleName() + ". Id: " + flowNode.getId() + ")");
    }

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
