package org.processmining.framework.models.fsm;

import org.processmining.framework.models.ModelGraphEdge;

/**
 * <p>Title: FSM transition</p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>
 * <p>Company: TU/e</p>
 *
 * @author Eric Verbeek
 * @version 1.0
 * <p>
 * Code rating: Red
 * <p>
 * Review rating: Red
 */
public class FSMTransition extends ModelGraphEdge {

    private String condition;

    public FSMTransition(FSMState fromState, FSMState toState, String condition) {
        super(fromState, toState);
        this.condition = condition;
        setDotAttribute("label", condition);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
        setDotAttribute("label", condition);
    }
}
