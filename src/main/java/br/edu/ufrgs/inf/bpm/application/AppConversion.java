package br.edu.ufrgs.inf.bpm.application;

import br.edu.ufrgs.inf.bpm.changes.prom.BpmnImportAdapter;
import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.exporting.yawl.YAWLExport;
import org.processmining.framework.models.bpmn.BpmnGraph;
import org.processmining.framework.models.bpmn.BpmnUtils;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class AppConversion {
    public static void main(String[] args) {
        BpmnImportAdapter bpmnImportAdapter = new BpmnImportAdapter();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/others/testData/bpmn/diagram2.bpmn");

            BpmnResult bpmnResult = (BpmnResult) bpmnImportAdapter.importFile(inputStream);

            BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
            YAWLExport yawlExport = new YAWLExport();
            OutputStream outputStream = new FileOutputStream("src/main/others/testData/bpmn/teste.yawl");
            for (ProvidedObject providedObjectBpmn : bpmnResult.getProvidedObjects()) {
                YAWLResult yawlResult = (YAWLResult) bpmnToYawl.convert(providedObjectBpmn);
                for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
                    yawlExport.export(providedObjectYawl, outputStream);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BpmnGraph importFile(InputStream input) throws IOException {
        BpmnGraph graph = null;
        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            doc = dbf.newDocumentBuilder().parse(input);
        } catch (Throwable x) {
            throw new IOException(x.getMessage());
        }

        if (doc != null) {
            graph = BpmnUtils.createBpmnGraph(doc);
        }

        return graph;
    }

}
