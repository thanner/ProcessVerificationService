package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model.VerificationElement;
import br.edu.ufrgs.inf.bpm.util.ResourceLoader;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationWrapper;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerificationGenerator {

    public static List<VerificationElement> generateVerification(String bpmnString) throws IOException, YSyntaxException {
        File tempFile = ResourceLoader.createResourceFile("yawlFile", ".yawl");
        TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);
        List<VerificationElement> verificationElementList = new ArrayList<>();

        // Conversion (BPMN - BPMN)
        ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
        List<BpmnResult> bpmnResultList = processModelBuilder.buildProcess(definitions);

        for (BpmnResult bpmnResult : bpmnResultList) {
            Map<String, String> bpmnIdMap = processModelBuilder.getIdMap();

            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            YAWLResult yawlResult = yawlBuilder.buildYawl(bpmnResult, tempFile);
            Map<String, String> bpmnYawlIdMap = yawlBuilder.buildBpmnYawlIdMap(yawlResult, bpmnIdMap);

            // Verification
            VerificationWrapper verificationWrapper = new VerificationWrapper();
            verificationElementList.addAll(verificationWrapper.verify(tempFile, bpmnYawlIdMap));
        }

        return verificationElementList;
    }

}
