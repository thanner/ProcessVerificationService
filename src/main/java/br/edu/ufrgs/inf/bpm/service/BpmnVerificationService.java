package br.edu.ufrgs.inf.bpm.service;

import br.edu.ufrgs.inf.bpm.builder.VerificationGenerator;
import br.edu.ufrgs.inf.bpm.verificationmessages.TBpmnVerification;
import br.edu.ufrgs.inf.bpm.wrapper.JsonWrapper;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Service;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import javax.ws.rs.core.Response;
import java.io.IOException;

@Api("/service")
@Service
public class BpmnVerificationService implements IBpmnVerificationService {

    @Override
    public Response generateBpmnVerification(String bpmnString) {
        try {
            TBpmnVerification tVerification = VerificationGenerator.generateVerification(bpmnString);
            return Response.ok().entity(JsonWrapper.getJson(tVerification)).build();
        } catch (IOException | YSyntaxException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}