package br.edu.ufrgs.inf.bpm.wrapper.elementType;

import org.processmining.framework.models.bpmn.BpmnTaskType;

public enum ActivityType {
    TTask(BpmnTaskType.Task), TActivity(BpmnTaskType.Activity), TSubProcess(BpmnTaskType.SubProcess);

    private final BpmnTaskType bpmnTaskType;

    ActivityType(BpmnTaskType bpmnTaskType) {
        this.bpmnTaskType = bpmnTaskType;
    }

    public BpmnTaskType getValue() {
        return bpmnTaskType;
    }
}
