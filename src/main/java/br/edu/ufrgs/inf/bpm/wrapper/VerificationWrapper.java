package br.edu.ufrgs.inf.bpm.wrapper;

import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import org.apache.commons.io.FileUtils;
import org.omg.spec.bpmn._20100524.model.*;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.YVerificationHandler;
import org.yawlfoundation.yawl.util.YVerificationMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerificationWrapper {

    public List<TMessage> verify(File yawlFile, Map<String, TBaseElement> bpmnYawlIdMap) throws IOException, YSyntaxException {

        String spec = FileUtils.readFileToString(yawlFile, "UTF-8");
        YVerificationHandler handler = new YVerificationHandler();

        List<TMessage> verificationElementList = new ArrayList<>();
        List<YSpecification> specifications = YMarshal.unmarshalSpecifications(spec);
        for (YSpecification ySpecification : specifications) {
            ySpecification.verify(handler);
            if (handler.hasMessages()) {
                // ySpecification.getID() e ySpecification.getName()
                verificationElementList.addAll(getVerificationElement(bpmnYawlIdMap, handler));
            }
        }

        return verificationElementList;
    }

    private List<TMessage> getVerificationElement(Map<String, TBaseElement> bpmnYawlIdMap, YVerificationHandler handler) {
        List<TMessage> tMessageList = new ArrayList<>();

        for (YVerificationMessage message : handler.getMessages()) {
            TMessage tMessage = new TMessage();
            TBaseElement tBaseElement = null;

            String processElementId = "Process";
            if (message.getSource() instanceof YAtomicTask) {
                YAtomicTask task = (YAtomicTask) message.getSource();
                tBaseElement = bpmnYawlIdMap.get(task.getID());
                if (tBaseElement != null) {
                    processElementId = tBaseElement.getId();
                }
            }

            tMessage.setProcessElementId(processElementId);
            tMessage.setDescription(createDescription(message, tBaseElement));
            tMessageList.add(tMessage);
        }

        return tMessageList;
    }

    private String createDescription(YVerificationMessage message, TBaseElement tBaseElement) {
        String description = message.getMessage();
        String atomicTask = message.getSource().toString();
        MessageHandler messageHandler = new MessageHandler();

        if (tBaseElement != null) {
            String elementDescription = "";
            if (tBaseElement instanceof TActivity) {
                elementDescription = messageHandler.handleActivityError((TActivity) tBaseElement);
            } else if (tBaseElement instanceof TGateway) {
                elementDescription = messageHandler.handleGatewayError((TGateway) tBaseElement);
            } else if (tBaseElement instanceof TEvent) {
                elementDescription = messageHandler.handleEventError((TEvent) tBaseElement);
            } else if (tBaseElement instanceof TSequenceFlow) {
                elementDescription = messageHandler.handleSequenceFlowError((TSequenceFlow) tBaseElement);
            }

            if (!elementDescription.isEmpty()) {
                description = description.replaceAll(atomicTask, elementDescription);
            }
        }

        return description;
    }

}
