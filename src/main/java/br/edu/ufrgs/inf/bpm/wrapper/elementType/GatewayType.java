package br.edu.ufrgs.inf.bpm.wrapper.elementType;

import org.processmining.framework.models.bpmn.BpmnGatewayType;

public enum GatewayType {
    TExclusiveGateway(BpmnGatewayType.XOR),
    TInclusiveGateway(BpmnGatewayType.OR),
    TParallelGateway(BpmnGatewayType.AND),
    TEventBasedGateway(BpmnGatewayType.XOR);
    // TODO: TEventBasedGateway?

    private final BpmnGatewayType bpmnGatewayType;

    GatewayType(BpmnGatewayType bpmnGatewayType) {
        this.bpmnGatewayType = bpmnGatewayType;
    }

    public BpmnGatewayType getValue() {
        return bpmnGatewayType;
    }
}
