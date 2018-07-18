package br.edu.ufrgs.inf.bpm.wrapper.elementType;

import org.processmining.framework.models.bpmn.BpmnEventType;

public enum EventType {
    TStartEvent(BpmnEventType.Start),
    TEndEvent(BpmnEventType.End),
    TIntermediateCatchEvent(BpmnEventType.Intermediate),
    TIntermediateThrowEvent(BpmnEventType.Intermediate),
    TBoundaryEvent(BpmnEventType.Intermediate);
    // TODO: TBoundaryEvent?

    private final BpmnEventType bpmnEventType;

    EventType(BpmnEventType bpmnEventType) {
        this.bpmnEventType = bpmnEventType;
    }

    public BpmnEventType getValue() {
        return bpmnEventType;
    }
}
