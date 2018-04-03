package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.builder.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.builder.YAWLBuilder;
import br.edu.ufrgs.inf.bpm.util.ResourceLoader;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationElement;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationWrapper;
import com.google.gson.Gson;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

// The Java class will be hosted at the URI path "/application"
@Path("/application")
public class ApplicationRest {

    @POST
    @Path("/hasConnected")
    public Response hasConnected() {
        return Response.ok().build();
    }

    @POST
    @Path("/getVerification")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getVerificationXml(String bpmnString) {
        List<VerificationElement> verifications;
        try {
            File tempFile = ResourceLoader.createResourceFile("yawlFile", ".yawl");
            TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);

            // Conversion (BPMN - BPMN)
            ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
            BpmnResult bpmnResult = processModelBuilder.buildProcess(definitions);

            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            yawlBuilder.buildYawl(bpmnResult, tempFile);

            // Verification
            VerificationWrapper verificationWrapper = new VerificationWrapper();
            verifications = verificationWrapper.verify(tempFile);

            verifications.forEach(System.out::println);
        } catch (IOException | YSyntaxException e) {
            return Response.serverError().build();
        }
        Gson gson = new Gson();
        return Response.ok().entity(gson.toJson(verifications)).build();
    }

}