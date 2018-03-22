package br.edu.ufrgs.inf.bpm;

import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.framework.models.bpmn.BpmnGraph;
import org.processmining.framework.models.bpmn.BpmnProcessModel;
import org.processmining.framework.models.bpmn.BpmnTask;
import org.processmining.framework.models.yawl.YAWLModel;

import java.io.StringWriter;

public class Ferramenta {
    public static void main(String[] args) {
        BpmnProcessModel processModel = new BpmnProcessModel("parentId");

        // Criando processo
        BpmnTask bpmnTask = new BpmnTask("taskId");
        bpmnTask.setName("My first task");
        processModel.addNode(bpmnTask);

        // Gerando BpmnGraph
        BpmnGraph bpmnGraph = new BpmnGraph("Process", processModel);

        // Transformando em YAWL
        BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
        YAWLModel yawlModel = bpmnToYawl.convert(bpmnGraph);

        String yawlXML = yawlModel.writeToYAWL(new StringWriter());
        System.out.println(yawlXML);

        // Salva yawlXML em alguma espécie de buffer
        // Gera uma estrutura yawlfoundation a partir do xml/buffer (YSpecification, YDecomposition)
        // para estrutura/cada elemento, faz a verificação
    }
}
