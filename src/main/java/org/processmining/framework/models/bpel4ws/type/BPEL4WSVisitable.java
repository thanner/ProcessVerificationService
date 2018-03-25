/*
 * Created on 16-02-2005
 *
 */
package org.processmining.framework.models.bpel4ws.type;


/**
 * @author Kristian Bisgaard Lassen
 */
public interface BPEL4WSVisitable {

    /**
     * @param visitor
     */
    void acceptVisitor(BPEL4WSVisitor visitor);

}
