/*
package br.edu.ufrgs.inf.bpm.serviceregistry;

import br.edu.ufrgs.inf.bpm.builder.VerificationGenerator;
import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model.VerificationElement;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import java.io.IOException;
import java.util.List;

@RestController
public class Service {

    @RequestMapping(value = "/hasConnected", method = RequestMethod.GET)
    public boolean hasConnected() {
        return true;
    }

    @RequestMapping(value = "/getVerification", method = RequestMethod.POST)
    public List<VerificationElement> getVerificationXml(@RequestBody String bpmnString) throws IOException, YSyntaxException {
        return VerificationGenerator.generateVerification(bpmnString);
    }

}
*/