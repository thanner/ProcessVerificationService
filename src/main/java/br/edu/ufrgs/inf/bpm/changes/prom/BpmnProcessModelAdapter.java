package br.edu.ufrgs.inf.bpm.changes.prom;

import org.processmining.framework.models.bpmn.BpmnEdge;
import org.processmining.framework.models.bpmn.BpmnEvent;
import org.processmining.framework.models.bpmn.BpmnObject;
import org.processmining.framework.models.bpmn.BpmnProcessModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

// FIXME: Pode invalidar process model builder
public class BpmnProcessModelAdapter extends BpmnProcessModel {

    public BpmnProcessModelAdapter(String parentId, Element element) {
        super(parentId, element);
    }

    public BpmnProcessModelAdapter(String parentId) {
        super(parentId);
    }

    @Override
    protected void parseCurrentLevel(Element element) {
        // get all child nodes of the parent element
        NodeList childNodes = element.getChildNodes();
        int childrenNum = childNodes.getLength();

        for (int i = 0; i < childrenNum; i++) {
            Node nd = childNodes.item(i);
            if (nd instanceof Element) {
                parseElement((Element) nd);

                Element childNode = (Element) nd;
                if (childNode.getTagName().equals("bpmn:process")) {
                    parseCurrentLevel(childNode);
                }
            }
        }
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setStart(BpmnEvent start) {
        this.start = start;
    }

    public void setEnd(BpmnEvent end) {
        this.end = end;
    }

    public HashMap<String, BpmnObject> getNodes() {
        return nodes;
    }

    public void putNode(String id, BpmnObject bpmnObject) {
        nodes.put(id, bpmnObject);
    }

    public HashMap<String, BpmnEdge> getEdges() {
        return edges;
    }

    public void putEdge(String id, BpmnEdge bpmnEdge) {
        edges.put(id, bpmnEdge);
    }

    /*
    @Override
    protected BpmnElement parseElement(Element element) {
        // TODO: SET TYPE das classes estão todas comentadas (7 no total). Talvez a soluação ideal seja herdar essa classe e sobrescrever os métodos
        String tag = element.getTagName();

        // events
        // all elements starting with Start, Intermediate or End
        if (tag.equals(BpmnXmlTags.BPMN_START)
                || tag.equals(BpmnXmlTags.BPMN_INTERMEDIATE)
                || tag.equals(BpmnXmlTags.BPMN_END)) {
            BpmnEvent event = new BpmnEvent(element);
            String id = event.getId();
            // set the type by the starting tag name
            // event.setTypeTag(BpmnEventType.valueOf(tag));
            // set the parent id
            event.setpid(this.parentId);
            // save the node
            nodes.put(id, event);
            if (tag.equals(BpmnXmlTags.BPMN_START)) {
                this.start = event;
            } else if (tag.equals(BpmnXmlTags.BPMN_END)) {
                this.end = event;
            }
            return event;
        } else if (tag.equals(BpmnXmlTags.BPMN_TASK)) {
            // Task and activities starting with the tag "Task"
            BpmnTask task = new BpmnTask(element);
            String id = task.getId();
            // set the type by the starting tag name
            // task.setTypeTag(BpmnTaskType.valueOf(tag));
            // set the parent id
            task.setpid(this.parentId);
            // save the node
            nodes.put(id, task);

            //handle its child of intermediate event type
            NodeList childNodes = element.getElementsByTagName(BpmnXmlTags.BPMN_INTERMEDIATE);
            int childrenNum = childNodes.getLength();
            for (int i = 0; i < childrenNum; i++) {
                Node nd = childNodes.item(i);
                if (nd instanceof Element) {
                    BpmnEvent event = (BpmnEvent) parseElement((Element) nd);
                    event.setLane(task.getLane());
                    if (event != null) {
                        String edgeId = event.getId() + "_" + id;
                        BpmnEdge edge = new BpmnEdge(event.getId(), id);
                        //edge.setType(BpmnEdgeType.Flow);
                        // set the parent id
                        edge.setpid(this.parentId);
                        edge.setId(edgeId);
                        // save the node
                        edges.put(edgeId, edge);
                    }
                }
            }
            return task;
        } else if (tag.equals(BpmnXmlTags.BPMN_SUBPROCESS)) {
            // sub process
            BpmnSubProcess sub = new BpmnSubProcess(element);
            String id = sub.getId();
            // sub.setTypeTag(BpmnTaskType.valueOf(tag));
            // set the parent id
            sub.setpid(this.parentId);
            // save the node
            subProcesses.put(id, sub);
            return sub;
        } else if (tag.equals(BpmnXmlTags.BPMN_GATEWAY)) {
            // gateways
            BpmnGateway gw = new BpmnGateway(element);
            String id = gw.getId();
            // set the parent id
            gw.setpid(this.parentId);
            // save the node
            nodes.put(id, gw);
            return gw;
        } else if (tag.equals(BpmnXmlTags.BPMN_POOL)) {
            // lane and pool
            BpmnSwimPool pool = new BpmnSwimPool(element);
            // pool.setType(BpmnSwimType.valueOf(tag));
            String poolid = pool.getId();
            // set the parent id
            pool.setpid(this.parentId);
            // save the node
            nodes.put(poolid, pool);
            return pool;
        } else if (tag.equals(BpmnXmlTags.BPMN_LANE)) {
            // lane and pool
            BpmnSwimLane lane = new BpmnSwimLane(element);
            //lane.setType(BpmnSwimType.valueOf(tag));
            String laneid = lane.getId();
            // set the parent id
            lane.setpid(this.parentId);
            // save the node
            nodes.put(laneid, lane);
            return lane;
        } else if (tag.equals(BpmnXmlTags.BPMN_FLOW)
                || tag.equals(BpmnXmlTags.BPMN_MESSAGE)) {
            // connections
            BpmnEdge edge = new BpmnEdge(element);
            //edge.setType(BpmnEdgeType.valueOf(tag));
            String id = edge.getId();
            // set the parent id
            edge.setpid(this.parentId);
            // save the node
            edges.put(id, edge);
            return edge;
        }
        return null;
    }
    */
}
