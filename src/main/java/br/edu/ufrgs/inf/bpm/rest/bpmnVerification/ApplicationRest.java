package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

// import br.edu.ufrgs.inf.bpm.wrapper.StardogWrapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

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
    public Response getBpmnXml(String bpmnString) throws IOException {
        /*
        StardogWrapper stardogWrapper = new StardogWrapper();
        String validation = stardogWrapper.getValidation();
        return Response.ok().entity(validation).build();
        */
        return Response.ok().build();
    }

}