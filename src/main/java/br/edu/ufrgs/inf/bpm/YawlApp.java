package br.edu.ufrgs.inf.bpm;

import br.edu.ufrgs.inf.bpm.rest.bpmnVerification.ApplicationRest;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class YawlApp {

    public static void main(String[] args) {
        try {
            ApplicationRest applicationRest = new ApplicationRest();
            String bpmnString = FileUtils.readFileToString(new File("src/main/others/testData/bpmn/diagram7.bpmn"), "UTF-8");
            applicationRest.getVerificationXml(bpmnString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
