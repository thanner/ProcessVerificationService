package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.builder.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.builder.YAWLBuilder;
import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.model.VerificationElement;
import br.edu.ufrgs.inf.bpm.util.ResourceLoader;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.JsonWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationWrapper;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
            Map<String, String> bpmnIdMap = processModelBuilder.getIdMap();

            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            YAWLResult yawlResult = yawlBuilder.buildYawl(bpmnResult, tempFile);
            Map<String, String> bpmnYawlIdMap = yawlBuilder.buildBpmnYawlIdMap(yawlResult, bpmnIdMap);

            // Verification
            VerificationWrapper verificationWrapper = new VerificationWrapper();
            verifications = verificationWrapper.verify(tempFile, bpmnYawlIdMap);

            verifications.forEach(System.out::println);
        } catch (IOException | YSyntaxException e) {
            return Response.serverError().build();
        }

        return Response.ok().entity(JsonWrapper.getJson(verifications)).build();
    }

}