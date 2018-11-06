package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.util.Paths;
import br.edu.ufrgs.inf.bpm.util.ResourceLoader;
import br.edu.ufrgs.inf.bpm.verificationmessages.TBpmnVerification;
import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.YAWLVerificationWrapper;
import org.apache.commons.io.FileUtils;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VerificationGenerator {

    public static TBpmnVerification generateVerification(String bpmnString) throws IOException, YSyntaxException {
        TDefinitions tDefinitions = JaxbWrapper.convertXMLToObject(bpmnString);
        TBpmnVerification tBpmnVerification = new TBpmnVerification();

        // BPMN Verification

        BPMNVerification processModelVerification = new BPMNVerification();
        filterUniques(tBpmnVerification, processModelVerification.verify(tDefinitions));

        // YAWL Verification

        // Conversion (BPMN - BPMN)
        ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
        List<BpmnResult> bpmnResultList = processModelBuilder.buildProcess(tDefinitions);

        Map<String, TBaseElement> bpmnResultBpmnMap = processModelBuilder.getBpmnResultBpmnMap();
        File tempFile = ResourceLoader.createResourceFile("yawlFile", ".yawl");
        for (BpmnResult bpmnResult : bpmnResultList) {
            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            YAWLResult yawlResult = yawlBuilder.buildYawl(bpmnResult, tempFile);
            Map<String, TBaseElement> bpmnYawlIdMap = yawlBuilder.buildBpmnYawlIdMap(yawlResult, bpmnResultBpmnMap);

            //handleDecomposition(tempFile);

            // Verification
            YAWLVerificationWrapper yawlVerificationWrapper = new YAWLVerificationWrapper();
            filterUniques(tBpmnVerification, yawlVerificationWrapper.verify(tempFile, bpmnYawlIdMap));
        }

        // See the YAWL
        createYawlFile(tempFile);

        return tBpmnVerification;
    }


    private static void createYawlFile(File tempFile) throws IOException {
        String text = FileUtils.readFileToString(tempFile, "UTF-8");
        File file = new File(Paths.LocalOthersPath + "/TestData/specification.yawl");
        FileUtils.writeStringToFile(file, text, "UTF-8");
    }

    private static void filterUniques(TBpmnVerification tBpmnVerification, List<TMessage> newMessages) {
        for (TMessage newMessage : newMessages) {
            boolean isNewMessage = true;
            for (TMessage message : tBpmnVerification.getMessageList()) {
                if (message.getProcessElementId().equals(newMessage.getProcessElementId()) && message.getDescription().equals(newMessage.getDescription())) {
                    isNewMessage = false;
                }
            }
            if (isNewMessage) {
                tBpmnVerification.getMessageList().add(newMessage);
            }
        }
    }

}
