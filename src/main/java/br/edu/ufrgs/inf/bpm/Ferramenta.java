package br.edu.ufrgs.inf.bpm;

import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.exporting.yawl.YAWLExport;
import org.processmining.framework.models.bpmn.BpmnGraph;
import org.processmining.framework.models.bpmn.BpmnUtils;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.importing.bpmn.BpmnImport;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class Ferramenta {
    public static void main(String[] args) {
        BpmnImport bpmnImport = new BpmnImport();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/others/testData/bpmn/diagram.bpmn");

            // TÁ RETORNANDO ERRADO
            BpmnResult bpmnResult = (BpmnResult) bpmnImport.importFile(inputStream);

            BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
            YAWLExport yawlExport = new YAWLExport();
            OutputStream outputStream = new FileOutputStream("src/main/others/testData/bpmn/teste.yawl");
            for (ProvidedObject providedObjectBpmn : bpmnResult.getProvidedObjects()) {
                YAWLResult yawlResult = (YAWLResult) bpmnToYawl.convert(providedObjectBpmn);
                for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
                    yawlExport.export(providedObjectYawl, outputStream);
                    System.out.println("a");
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
