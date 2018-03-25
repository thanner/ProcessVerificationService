/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package org.processmining.framework.models.bpel;

import org.processmining.framework.models.bpel.visit.BPELVisitor;
import org.w3c.dom.Element;

/**
 * <p>Title: BPELEmpty</p>
 * <p>
 * <p>Description: Class for the BPEL empty activity.</p>
 * <p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>
 * <p>Company: TU/e</p>
 *
 * @author Eric Verbeek
 * @version 1.0
 */
public class BPELEmpty extends BPELActivity {

    public BPELEmpty(Element element) {
        super(element);
    }

    public BPELEmpty(String name) {
        super(BPELConstants.stringEmpty, name);
    }

    public BPELEmpty cloneActivity() {
        BPELEmpty clone = new BPELEmpty(element.getAttribute(BPELConstants.stringName));
        clone.cloneLinks(this);
        return clone;
    }

    public void acceptVisitor(BPELVisitor visitor) {
        visitor.visit(this);
    }
}