package br.edu.ufrgs.inf.bpm;

import org.jdom2.Element;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;

import java.io.IOException;

public class TwitterService extends InterfaceBWebsideController {

    // holds a session handle to the engine
    private String handle = null;
    private final String engineUser = "twitterService";
    private final String enginePassword = "yTwitter";

    public void handleEnabledWorkItemEvent(WorkItemRecord workItemRecord) {
        try {

            // connect only if not already connected
            if (!connected()) {
                handle = connect(engineUser, enginePassword);
            }

            // checkout ... process ... checkin
            workItemRecord = checkOut(workItemRecord.getID(), handle);
            String result = updateStatus(workItemRecord);
            checkInWorkItem(workItemRecord.getID(), workItemRecord.getDataList(),
                    getOutputData(workItemRecord.getTaskName(), result), null, handle);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private String updateStatus(WorkItemRecord workItemRecord) {
        return "Testado";
    }

    // have to implement abstract method , but have no need for this event
    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {
    }

    // these parameters are automatically inserted (in the Editor) into a task
    // decomposition when this service is selected from the list
    public YParameter[] describeRequiredParams() {
        YParameter[] params = new YParameter[4];
        params[0] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        params[0].setDataTypeAndName("string", "status", XSD_NAMESPACE);
        params[0].setDocumentation("The status message to post to Twitter");

        params[1] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        params[1].setDataTypeAndName("string", "userid", XSD_NAMESPACE);
        params[1].setDocumentation("Your Twitter ID");

        params[2] = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        params[2].setDataTypeAndName("string", "password", XSD_NAMESPACE);
        params[2].setDocumentation("Your Twitter password");

        params[3] = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
        params[3].setDataTypeAndName("string", "result", XSD_NAMESPACE);
        params[3].setDocumentation("The status result or error message returned");
        return params;
    }

    private boolean connected() throws IOException {
        return handle != null && checkConnection(handle);
    }

    private Element getOutputData(String taskName, String data) {
        Element output = new Element(taskName);
        Element result = new Element("result");
        result.setText(data);
        output.addContent(result);
        return output;
    }

}