package br.edu.ufrgs.inf.bpm.wrapper;

import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model.VerificationElement;
import org.apache.commons.io.FileUtils;
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

    public List<VerificationElement> verify(File yawlFile, Map<String, String> bpmnYawlIdMap) throws IOException, YSyntaxException {

        String spec = FileUtils.readFileToString(yawlFile, "UTF-8");
        YVerificationHandler handler = new YVerificationHandler();

        List<VerificationElement> verificationElementList = new ArrayList<>();
        List<YSpecification> specifications = YMarshal.unmarshalSpecifications(spec);
        for (YSpecification ySpecification : specifications) {
            ySpecification.verify(handler);
            if (handler.hasMessages()) {
                verificationElementList.add(getVerificationElement(ySpecification.getID(), bpmnYawlIdMap, ySpecification.getName(), handler));
            }
        }

        return verificationElementList;
    }

    private VerificationElement getVerificationElement(String id, Map<String, String> bpmnYawlIdMap, String name, YVerificationHandler handler) {
        VerificationElement verificationElement = new VerificationElement();

        verificationElement.setId(id);
        verificationElement.setName(name);

        for (YVerificationMessage message : handler.getMessages()) {
            String elementId = "";
            if (message.getSource() instanceof YAtomicTask) {
                YAtomicTask task = (YAtomicTask) message.getSource();
                elementId = bpmnYawlIdMap.get(task.getID());
            }

            verificationElement.addMessage(elementId, message.getMessage());
        }

        return verificationElement;
    }

}
