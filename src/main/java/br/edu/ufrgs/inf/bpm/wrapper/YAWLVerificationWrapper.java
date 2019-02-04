package br.edu.ufrgs.inf.bpm.wrapper;

import br.edu.ufrgs.inf.bpm.messages.MessageType;
import br.edu.ufrgs.inf.bpm.verificationmessages.TMessage;
import org.apache.commons.io.FileUtils;
import org.omg.spec.bpmn._20100524.model.*;
import org.yawlfoundation.yawl.analyser.YAnalyser;
import org.yawlfoundation.yawl.analyser.YAnalyserOptions;
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

public class YAWLVerificationWrapper {

    private String baseProcessElementId = "Process";
    private String source = "YAWL Verification";
    private Map<String, TBaseElement> bpmnYawlIdMap;

    public List<TMessage> verify(File yawlFile, Map<String, TBaseElement> bpmnYawlIdMap) throws IOException, YSyntaxException {
        String spec = FileUtils.readFileToString(yawlFile, "UTF-8");
        this.bpmnYawlIdMap = bpmnYawlIdMap;
        YVerificationHandler handler = new YVerificationHandler();

        List<TMessage> yawlMessageList = new ArrayList<>();
        List<YSpecification> specifications = YMarshal.unmarshalSpecifications(spec);
        for (YSpecification ySpecification : specifications) {
            ySpecification.verify(handler);
            yawlMessageList.addAll(getVerificationMessageList(handler));

            if (handler.hasErrors()) {
                yawlMessageList.add(getVerificationErrorsFoundMessage());
            } else {
                yawlMessageList.addAll(getAnalysisMessageList(ySpecification));
            }
        }

        return yawlMessageList;
    }

    private List<TMessage> getVerificationMessageList(YVerificationHandler handler) {
        List<TMessage> tMessageList = new ArrayList<>();

        for (YVerificationMessage message : handler.getMessages()) {
            tMessageList.add(getMessage(message));
        }

        return tMessageList;
    }

    private TMessage getVerificationErrorsFoundMessage() {
        return getMessage("It was not possible to verify soundness in the model. Please correct the errors identified in the YAWL before this is possible.");
    }

    private TMessage getMessage(YVerificationMessage message) {
        TMessage tMessage = new TMessage();
        TBaseElement tBaseElement = null;

        String processElementId = baseProcessElementId;
        if (message.getSource() instanceof YAtomicTask) {
            YAtomicTask task = (YAtomicTask) message.getSource();
            tBaseElement = bpmnYawlIdMap.get(task.getID());
            if (tBaseElement != null) {
                processElementId = tBaseElement.getId();
            }
        }

        tMessage.setProcessElementId(processElementId);
        tMessage.setDescription(createDescription(message, tBaseElement));
        tMessage.setSource(source);
        tMessage.setMessageType(MessageType.STRUCTURE.getValue());

        return tMessage;
    }

    private String createDescription(YVerificationMessage message, TBaseElement tBaseElement) {
        String description = message.getMessage();
        String yawlElement = message.getSource().toString();

        String elementDescription = getElementDescription(tBaseElement);

        if (!elementDescription.isEmpty()) {
            description = description.replaceAll(yawlElement, elementDescription);
        }

        description = description.replaceAll("Error: ", "");

        return description;
    }

    private String getElementDescription(TBaseElement tBaseElement) {
        MessageHandler messageHandler = new MessageHandler();
        String elementDescription = "";
        if (tBaseElement != null) {
            if (tBaseElement instanceof TActivity) {
                elementDescription = messageHandler.handleActivityError((TActivity) tBaseElement);
            } else if (tBaseElement instanceof TEvent) {
                elementDescription = messageHandler.handleEventError((TEvent) tBaseElement);
            } else if (tBaseElement instanceof TGateway) {
                elementDescription = messageHandler.handleGatewayError((TGateway) tBaseElement);
            } else if (tBaseElement instanceof TSequenceFlow) {
                elementDescription = messageHandler.handleSequenceFlowError((TSequenceFlow) tBaseElement);
            } else if (tBaseElement instanceof TFlowNode) {
                elementDescription = messageHandler.handleFlowNodeError((TFlowNode) tBaseElement);
            } else {
                elementDescription = messageHandler.handleBaseElementError(tBaseElement);
            }
        } else {
            elementDescription = "Process";
        }
        return elementDescription;
    }

    private List<TMessage> getAnalysisMessageList(YSpecification ySpecification) {
        List<TMessage> tMessageList = new ArrayList<>();

        YAnalyserOptions yAnalyserOptions = new YAnalyserOptions();
        yAnalyserOptions.enableYawlReductionRules(true);
        yAnalyserOptions.enableResetReductionRules(true);
        //yAnalyserOptions.enableResetWeakSoundness(true);
        yAnalyserOptions.enableResetSoundness(true);
        yAnalyserOptions.enableResetCancellation(true);
        yAnalyserOptions.enableResetOrJoin(true);
        yAnalyserOptions.enableResetOrjoinCycle(true);

        YAnalyser yAnalyser = new YAnalyser();
        String analysisXML = yAnalyser.analyse(ySpecification, yAnalyserOptions);
        AnalysisHelper analysisHelper = new AnalysisHelper();
        List<String> analysisList = analysisHelper.parseRawResultsIntoList(analysisXML);
        for (String analysis : analysisList) {
            tMessageList.add(getMessage(analysis));
        }

        return tMessageList;
    }

    private TMessage getMessage(String message) {
        return getMessage(message, baseProcessElementId);
    }

    private TMessage getMessage(String message, String processElementId) {
        TMessage tMessage = new TMessage();

        tMessage.setProcessElementId(processElementId);
        tMessage.setDescription(updateMessage(message));
        tMessage.setSource(source);
        tMessage.setMessageType(MessageType.STRUCTURE.getValue());

        return tMessage;
    }

    private String updateMessage(String message) {
        message = message.replaceAll("The net root", "The process");
        message = message.replaceAll("ResetNet Analysis Warning: ", "");
        message = message.replaceAll("has dead tasks", "has unreachable elements");
        message = removeElements(message);

        for (String bpmnKey : bpmnYawlIdMap.keySet()) {
            Object o = bpmnYawlIdMap.get(bpmnKey);
            if (o != null) {
                if (o instanceof TFlowElement) {
                    TFlowElement tFlowElement = (TFlowElement) o;
                    message = message.replaceAll(bpmnKey, getElementDescription(tFlowElement));
                }
            } else {
                message = message.replaceAll(bpmnKey + ",", "");
                message = message.replaceAll(bpmnKey, "");
            }
        }
        return message;
    }

    private String removeElements(String message) {
        message = message.replaceAll("AtomicTask:", "");
        message = message.replaceAll("CompositeTask:", "");
        message = message.replaceAll("Condition:", "");
        message = message.replaceAll("Task:", "");
        return message;
    }

}
