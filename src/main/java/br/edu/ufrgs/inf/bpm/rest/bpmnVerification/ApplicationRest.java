package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.builder.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.builder.YAWLBuilder;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationElement;
import br.edu.ufrgs.inf.bpm.wrapper.VerificationWrapper;
import org.processmining.mining.bpmnmining.BpmnResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("/getValidation")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getBpmnXml(String bpmnString) {

        String yawlFileName = "src/main/others/testData/bpmn/teste.yawl";
        try {

            TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);

            // Conversion (BPMN - BPMN)
            ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
            BpmnResult bpmnResult = processModelBuilder.buildProcess(definitions);

            // Conversion (BPMN - YAWL)
            YAWLBuilder yawlBuilder = new YAWLBuilder();
            yawlBuilder.buildYawl(bpmnResult, yawlFileName);

            // Verification
            VerificationWrapper verificationWrapper = new VerificationWrapper();
            List<VerificationElement> verifications = verificationWrapper.verify(yawlFileName);

            verifications.forEach(System.out::println);

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

}