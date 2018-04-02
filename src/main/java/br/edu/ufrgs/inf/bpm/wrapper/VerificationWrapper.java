package br.edu.ufrgs.inf.bpm.wrapper;

import org.apache.commons.io.FileUtils;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.YVerificationHandler;
import org.yawlfoundation.yawl.util.YVerificationMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerificationWrapper {

    public List<VerificationElement> verify(String yawlFileName) throws IOException, YSyntaxException {

        String spec = FileUtils.readFileToString(new File(yawlFileName), "UTF-8");
        YVerificationHandler handler = new YVerificationHandler();

        List<VerificationElement> verificationElementList = new ArrayList<>();
        List<YSpecification> specifications = YMarshal.unmarshalSpecifications(spec);
        for (YSpecification ySpecification : specifications) {
            ySpecification.verify(handler);
            if (handler.hasMessages()) {
                verificationElementList.add(getVerificationElement(ySpecification, handler));
            }
        }


        return verificationElementList;
    }

    private VerificationElement getVerificationElement(YSpecification ySpecification, YVerificationHandler handler) {
        VerificationElement verificationElement = new VerificationElement();

        verificationElement.setId(ySpecification.getID());
        verificationElement.setName(ySpecification.getName());
        for (YVerificationMessage message : handler.getMessages()) {
            verificationElement.addMessage(message.getMessage());
        }

        return verificationElement;
    }

}
