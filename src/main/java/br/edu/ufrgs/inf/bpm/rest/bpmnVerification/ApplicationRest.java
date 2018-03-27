package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.builder.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import org.processmining.mining.MiningResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);

        ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
        MiningResult processModel = processModelBuilder.buildProcess(definitions);

        System.out.println("Fim");

        //return Response.ok().build();
        return null;
    }

}