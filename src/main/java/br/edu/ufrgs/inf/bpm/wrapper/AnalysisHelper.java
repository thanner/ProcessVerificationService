package br.edu.ufrgs.inf.bpm.wrapper;

import org.yawlfoundation.yawl.editor.ui.util.UserSettings;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;

import java.util.*;

public class AnalysisHelper {
    protected List<String> parseRawResultsIntoList(String rawAnalysisXML) {
        if (StringUtil.isNullOrEmpty(rawAnalysisXML)) {
            return Collections.emptyList();
        } else if (!rawAnalysisXML.startsWith("<error>") && !rawAnalysisXML.startsWith("<cancelled>")) {
            List<String> resultList = new ArrayList();
            XNode parentNode = (new XNodeParser()).parse(rawAnalysisXML);
            if (parentNode != null) {
                this.parseResetNetResults(resultList, parentNode);
                this.parseWofYawlResults(resultList, parentNode);
            } else {
                resultList.add("Analysis Error: Malformed analysis results.");
            }

            return resultList;
        } else {
            return Arrays.asList(StringUtil.unwrap(rawAnalysisXML));
        }
    }

    protected void parseResetNetResults(List<String> resultsList, XNode parentNode) {
        XNode resetNode = parentNode.getChild("resetAnalysisResults");
        if (resetNode != null) {
            String cancelMsg = resetNode.getChildText("cancelled");
            if (cancelMsg != null) {
                resultsList.add(cancelMsg);
            } else {
                this.parseResetNetErrors(resultsList, resetNode);
                this.parseResetNetWarnings(resultsList, resetNode);
                if (UserSettings.getShowObservations()) {
                    this.parseResetNetObservations(resultsList, resetNode);
                }
            }
        }

    }

    protected void parseResetNetErrors(List<String> resultsList, XNode resetNode) {
        String prefix = "ResetNet Analysis Error: ";
        this.parseResultsIntoList("error", prefix, resultsList, resetNode);
    }

    protected void parseResetNetWarnings(List<String> resultsList, XNode resetNode) {
        String prefix = "ResetNet Analysis Warning: ";
        this.parseResultsIntoList("warning", prefix, resultsList, resetNode);
    }

    protected void parseResetNetObservations(List<String> resultsList, XNode resetNode) {
        String prefix = "ResetNet Analysis Observation: ";
        this.parseResultsIntoList("observation", prefix, resultsList, resetNode);
    }

    protected void parseWofYawlResults(List<String> resultsList, XNode parentNode) {
        XNode wofYawlNode = parentNode.getChild("wofYawlAnalysisResults");
        if (wofYawlNode != null) {
            this.parseWofYawlStructuralWarnings(resultsList, wofYawlNode);
            this.parseWofYawlBehaviouralWarnings(resultsList, wofYawlNode);
        }

    }

    protected void parseWofYawlStructuralWarnings(List<String> resultsList, XNode wofYawlNode) {
        String prefix = "WofYAWL Structural Warning: ";
        this.parseResultsIntoList("structure", prefix, resultsList, wofYawlNode);
    }

    protected void parseWofYawlBehaviouralWarnings(List<String> resultsList, XNode wofYawlNode) {
        String prefix = "WofYAWL Behavioural Warning: ";
        this.parseResultsIntoList("behavior", prefix, resultsList, wofYawlNode);
    }

    private void parseResultsIntoList(String childName, String prefix, List<String> resultsList, XNode node) {
        Iterator var5 = node.getChildren(childName).iterator();

        while (var5.hasNext()) {
            XNode childNode = (XNode) var5.next();
            String msg = childNode.getText();
            if (msg != null) {
                resultsList.add(prefix + msg);
            }
        }

    }
}
