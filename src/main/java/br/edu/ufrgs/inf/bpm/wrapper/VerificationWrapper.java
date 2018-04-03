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

    public List<VerificationElement> verify(File yawlFile) throws IOException, YSyntaxException {

        String spec = FileUtils.readFileToString(yawlFile, "UTF-8");
        YVerificationHandler handler = new YVerificationHandler();

        List<VerificationElement> verificationElementList = new ArrayList<>();
        List<YSpecification> specifications = YMarshal.unmarshalSpecifications(spec);
        for (YSpecification ySpecification : specifications) {
            ySpecification.verify(handler);
            if (handler.hasMessages()) {
                verificationElementList.add(getVerificationElement(ySpecification.getID(), ySpecification.getName(), handler));
            }

            /*
            for(YDecomposition yDecomposition : ySpecification.getDecompositions()){
                yDecomposition.verify(handler);
                if(handler.hasMessages()){
                    verificationElementList.add(getVerificationElement(yDecomposition.getID(), yDecomposition.getName(), handler));
                }
            }
            */
        }


        return verificationElementList;
    }

    private VerificationElement getVerificationElement(String id, String name, YVerificationHandler handler) {
        VerificationElement verificationElement = new VerificationElement();

        verificationElement.setId(id);
        verificationElement.setName(name);
        for (YVerificationMessage message : handler.getMessages()) {
            verificationElement.addMessage(message.getMessage());
        }

        return verificationElement;
    }

}
