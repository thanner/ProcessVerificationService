package br.edu.ufrgs.inf.bpm.changes.prom;

import org.processmining.framework.models.bpmn.BpmnGraph;
import org.processmining.framework.models.bpmn.BpmnProcessModel;
import org.processmining.framework.models.bpmn.BpmnUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BpmnUtilsAdapter extends BpmnUtils {

    public static BpmnGraph createBpmnGraph(Document var0) {
        Element var1 = var0.getDocumentElement();
        return createBpmnGraph(var1);
    }

    public static BpmnGraph createBpmnGraph(Element var0) {
        BpmnGraph var1 = null;
        BpmnProcessModel var2 = new BpmnProcessModelAdapter(null, var0);
        var1 = new BpmnGraph(null, var2);
        return var1;
    }

}
