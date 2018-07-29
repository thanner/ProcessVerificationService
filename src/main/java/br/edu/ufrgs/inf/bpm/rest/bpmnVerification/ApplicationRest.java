package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.builder.VerificationGenerator;
import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model.VerificationElement;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/application")
public class ApplicationRest {

    @POST
    @Path("/hasConnected")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean hasConnected() {
        return true;
    }

    @POST
    @Path("/getVerification")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<VerificationElement> getVerificationXml(String bpmnString) throws IOException, YSyntaxException {
        return VerificationGenerator.generateVerification(bpmnString);
    }

}