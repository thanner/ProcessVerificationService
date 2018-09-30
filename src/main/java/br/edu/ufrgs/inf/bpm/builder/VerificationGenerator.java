package br.edu.ufrgs.inf.bpm.builder;

import br.edu.ufrgs.inf.bpm.util.ResourceLoader;
import br.edu.ufrgs.inf.bpm.verificationmessages.TBpmnVerification;
import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationWrapper;
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
        File tempFile = ResourceLoader.createResourceFile("yawlFile", ".yawl");
        TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);
        TBpmnVerification tBpmnVerification = new TBpmnVerification();

        // Conversion (BPMN - BPMN)
        ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
        List<BpmnResult> bpmnResultList = processModelBuilder.buildProcess(definitions);
        Map<String, TBaseElement> bpmnResultBpmnMap = processModelBuilder.getBpmnResultBpmnMap();

        for (BpmnResult bpmnResult : bpmnResultList) {
            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            YAWLResult yawlResult = yawlBuilder.buildYawl(bpmnResult, tempFile);
            Map<String, TBaseElement> bpmnYawlIdMap = yawlBuilder.buildBpmnYawlIdMap(yawlResult, bpmnResultBpmnMap);

            // Verification
            VerificationWrapper verificationWrapper = new VerificationWrapper();
            filterUniques(tBpmnVerification, verificationWrapper.verify(tempFile, bpmnYawlIdMap));
        }

        return tBpmnVerification;
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
