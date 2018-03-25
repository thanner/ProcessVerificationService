package org.processmining.framework.models.fsm;

/**
 * <p>Title: FSMPayload</p>
 * <p>
 * <p>Description: Payload for an FSMState</p>
 * <p>
 * <p>Copyright: Copyright (c) 2004</p>
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
public abstract class FSMPayload implements Comparable {
    public abstract FSMPayload merge(FSMPayload payload);
}
