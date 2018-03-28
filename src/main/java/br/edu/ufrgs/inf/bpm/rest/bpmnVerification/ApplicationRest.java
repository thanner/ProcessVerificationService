package br.edu.ufrgs.inf.bpm.rest.bpmnVerification;

import br.edu.ufrgs.inf.bpm.bpmn.TDefinitions;
import br.edu.ufrgs.inf.bpm.builder.ProcessModelBuilder;
import br.edu.ufrgs.inf.bpm.wrapper.JaxbWrapper;
import org.processmining.converting.bpmn2yawl.BPMNToYAWL;
import org.processmining.exporting.yawl.YAWLExport;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.mining.bpmnmining.BpmnResult;
import org.processmining.mining.yawlmining.YAWLResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

        try {
            TDefinitions definitions = JaxbWrapper.convertXMLToObject(bpmnString);

            ProcessModelBuilder processModelBuilder = new ProcessModelBuilder();
            BpmnResult bpmnResult = processModelBuilder.buildProcess(definitions);

            BPMNToYAWL bpmnToYawl = new BPMNToYAWL();
            YAWLExport yawlExport = new YAWLExport();
            OutputStream outputStream = new FileOutputStream("src/main/others/testData/bpmn/teste.yawl");
            for (ProvidedObject providedObjectBpmn : bpmnResult.getProvidedObjects()) {
                YAWLResult yawlResult = (YAWLResult) bpmnToYawl.convert(providedObjectBpmn);
                for (ProvidedObject providedObjectYawl : yawlResult.getProvidedObjects()) {
                    yawlExport.export(providedObjectYawl, outputStream);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        //return Response.ok().build();
        return null;
    }

}