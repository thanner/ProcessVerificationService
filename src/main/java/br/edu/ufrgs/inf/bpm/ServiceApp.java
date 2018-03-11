package br.edu.ufrgs.inf.bpm;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;

public class ServiceApp {

    public static void main(String[] args) {
        TwitterService twitterService = new TwitterService();
        WorkItemRecord workItemRecord = new WorkItemRecord();
        twitterService.handleEnabledWorkItemEvent(workItemRecord);
    }

}
