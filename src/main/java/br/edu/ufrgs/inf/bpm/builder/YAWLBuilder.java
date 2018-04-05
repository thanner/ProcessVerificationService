package br.edu.ufrgs.inf.bpm.builder;

import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.exporting.yawl.YAWLExport;
import org.processmining.framework.models.yawl.YAWLDecomposition;
import org.processmining.framework.models.yawl.YAWLModel;
import org.processmining.framework.models.yawl.YAWLNode;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class YAWLBuilder {

    public YAWLResult buildYawl(BpmnResult bpmnResult, File yawlFile) throws IOException {
        BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
        YAWLExport yawlExport = new YAWLExport();
        YAWLResult yawlResult = null;
        OutputStream outputStream = new FileOutputStream(yawlFile);
        for (ProvidedObject providedObjectBpmn : bpmnResult.getProvidedObjects()) {
            yawlResult = (YAWLResult) bpmnToYawl.convert(providedObjectBpmn);
            for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
                yawlExport.export(providedObjectYawl, outputStream);
            }
        }
        return yawlResult;
    }

    public Map<String, String> buildBpmnYawlIdMap(YAWLResult yawlResult, Map<String, String> bpmnIdMap) {
        Map<String, String> bpmnYawlIdMap = new HashMap<>();

        for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
            for (Object object : providedObjectYawl.getObjects()) {
                if (object instanceof YAWLModel) {
                    YAWLModel model = (YAWLModel) object;
                    for (YAWLDecomposition yawlDecomposition : model.getDecompositions()) {
                        for (YAWLNode yawlNode : yawlDecomposition.getNodes()) {
                            bpmnYawlIdMap.put("Node" + yawlNode.getId(), bpmnIdMap.get(yawlNode.getID()));
                        }
                        /*
                        for(Object yawlEdgeObject : yawlDecomposition.getEdges()){
                            if(yawlEdgeObject instanceof  YAWLEdge) {
                                YAWLEdge yawlEdge = (YAWLEdge) yawlEdgeObject;
                                // bpmnYawlIdMap.put(yawlEdge.toString(), bpmnIdMap.get(yawlEdge.toString()));
                                // cannonName
                            }
                        }
                        */
                    }
                }
            }
        }
        return bpmnYawlIdMap;
    }

}
