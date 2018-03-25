package org.processmining.framework.models.bpel.visit;

/**
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface BPELVisitable {
    void acceptVisitor(BPELVisitor visitor);
}
