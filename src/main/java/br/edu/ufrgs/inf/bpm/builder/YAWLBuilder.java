package br.edu.ufrgs.inf.bpm.builder;

import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.exporting.yawl.YAWLExport;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class YAWLBuilder {

    public void buildYawl(BpmnResult bpmnResult, File yawlFile) throws IOException {
        BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
        YAWLExport yawlExport = new YAWLExport();
        OutputStream outputStream = new FileOutputStream(yawlFile);
        for (ProvidedObject providedObjectBpmn : bpmnResult.getProvidedObjects()) {
            YAWLResult yawlResult = (YAWLResult) bpmnToYawl.convert(providedObjectBpmn);
            for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
                yawlExport.export(providedObjectYawl, outputStream);
            }
        }
    }

}
