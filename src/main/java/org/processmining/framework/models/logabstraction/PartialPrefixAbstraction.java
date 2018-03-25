package org.processmining.framework.models.logabstraction;

import org.processmining.analysis.log.scale.ProcessInstanceScale;
import org.processmining.framework.log.AuditTrailEntryList;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.ProcessInstance;
import org.processmining.framework.ui.Message;

import java.io.IOException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * <p>
 * Description:
 * </p>
 * <p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PartialPrefixAbstraction extends PrefixAbstraction {

    private int partialPrefixLength;

    public PartialPrefixAbstraction(LogReader log, ProcessInstance pi,
                                    ProcessInstanceScale scale, int partialPrefixLength) {
        super(log, pi, scale);

        this.partialPrefixLength = (pi.getAuditTrailEntryList().size() < partialPrefixLength) ?
                pi.getAuditTrailEntryList().size() : partialPrefixLength;
    }


    protected FitnessResult calculateFitnessResult(LogAbstraction logAbstraction) {
        FitnessResult result = new FitnessResult();
        result.fitness = 0.0;
        result.doesFit = false;

        PartialPrefixAbstraction incompleteExecution = (PartialPrefixAbstraction) logAbstraction;
        if (incompleteExecution.getPi().getAuditTrailEntryList().size() >=
                getPi().getAuditTrailEntryList().size()) {
            return result;
        }

        AuditTrailEntryList auditTrailEntries = getPi().getAuditTrailEntryList();

        AuditTrailEntryList auditTrailEntriesOfIncompleteExecution = incompleteExecution.getPi().
                getAuditTrailEntryList();

        for (int i = 0; i < getPi().getAuditTrailEntryList().size(); i++) {
            boolean matchPartialPrefix = true;
            try {
                for (int j = 0; j < incompleteExecution.partialPrefixLength; ++j) {
                    if (!same(auditTrailEntries.get(i + j),
                            auditTrailEntriesOfIncompleteExecution.get(j))) {
                        matchPartialPrefix = false;
                        break;
                    }
                }

                if (matchPartialPrefix) {
                    result.fitness = 1.0;
                    result.doesFit = true;
                    result.lastFitIndex = i + incompleteExecution.partialPrefixLength - 1;
                    return result;
                }

            } catch (IOException ex) {
                Message.add("Error while reading log file", Message.ERROR);
                result.fitness = 0.0;
                result.doesFit = false;
                return result;
            } catch (IndexOutOfBoundsException ex) {
                Message.add("Error while reading log file", Message.ERROR);
                result.fitness = 0.0;
                result.doesFit = false;
                return result;
            }
        }
        return result;
    }

    protected int getPartialPrefixLength() {
        return partialPrefixLength;
    }


}
