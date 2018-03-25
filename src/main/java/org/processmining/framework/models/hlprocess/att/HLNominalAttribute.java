/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2007 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/
package org.processmining.framework.models.hlprocess.att;

import org.processmining.framework.models.hlprocess.HLAttribute;
import org.processmining.framework.models.hlprocess.HLID;
import org.processmining.framework.models.hlprocess.HLProcess;
import org.processmining.framework.models.hlprocess.HLTypes.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A nominal attribute is an enumeration of possible string values.
 * <p>The likelyhood, or frequency, of each of these values is captured
 * by the distribution of possible values.
 *
 * @see HLAttribute
 */
public class HLNominalAttribute extends HLAttribute {

    /**
     * Default constructor.
     *
     * @param aName the name of this attribute
     * @param aProc the high level process it belongs to
     */
    public HLNominalAttribute(String aName, HLProcess aProc) {
        super(aName, aProc);
        initialValue = new HLNominalValue("");
        possibleValues = new HLNominalDistribution();
    }

    /**
     * Constructor initializing the group with a given ID.
     * <p>Can be used when importing organizational models with existing IDs.
     *
     * @param name  the name of the group
     * @param aProc the high level process this group belongs to
     */

    public HLNominalAttribute(String aName, HLProcess aProc, HLID id) {
        super(aName, aProc, id);
        initialValue = new HLNominalValue("");
        possibleValues = new HLNominalDistribution();
    }


    /**
     * Constructor to create a fully-specified nominal data attribute.
     *
     * @param name   the name of the data attribute
     * @param values the possible values (first value in list represents the initial value)
     * @param aProc  the process this attribute belongs to
     */
    public HLNominalAttribute(String aName, ArrayList<String> values, HLProcess aProc) {
        this(aName, values, values.get(0), aProc);
    }

    /**
     * Constructor to create a fully-specified nominal data attribute.
     *
     * @param name    the name of the data attribute
     * @param values  the possible values
     * @param initVal the initial value for this attribute
     * @param aProc   the process this attribute belongs to
     */
    public HLNominalAttribute(String name, ArrayList<String> values, String initVal, HLProcess aProc) {
        this(name, aProc);
        initialValue = new HLNominalValue(initVal);
        possibleValues = new HLNominalDistribution();
        ((HLNominalDistribution) possibleValues).setPossibleValues(values);
    }

    /*
     * (non-Javadoc)
     * @see org.processmining.framework.models.hlprocess.HLAttribute#getPossibleValues()
     */
    public HLNominalDistribution getPossibleValues() {
        return (HLNominalDistribution) possibleValues;
    }

    /**
     * Directly specifies the possible values and their frequencies.
     *
     * @param values the possible values and their frequencies
     */
    public void setPossibleValues(HashMap<String, Integer> values) {
        getPossibleValues().setPossibleValues(values);
    }

    /*
     * (non-Javadoc)
     * @see org.processmining.framework.models.hlprocess.HLAttribute#getInitialValue()
     */
    public HLAttributeValue getInitialValue() {
        if (initialValue != null && initialValue instanceof HLNominalValue) {
            // if no initial value set but possible values have been specified,
            // then use the first possible value as initial value
            if (((HLNominalValue) initialValue).getValue().equals("") &&
                    ((HLNominalDistribution) possibleValues).getValues().size() > 0 &&
                    ((HLNominalDistribution) possibleValues).getValues().contains("") == false) {
                initialValue = new HLNominalValue(((HLNominalDistribution) possibleValues).getValues().get(0));
            }
            return initialValue;
        } else {
            return initialValue;
        }
    }

    /**
     * Directly specifies the string for the new initial value for this attribute.
     *
     * @param aValue the new initial value for the nominal data attribute
     * @see #setInitialValue(HLAttributeValue)
     */
    public void setInitialValue(String aValue) {
        initialValue = new HLNominalValue(aValue);
    }

    /**
     * Directly retrieves the string of the initial value for the associated data attribute.
     *
     * @return the nominal default value. Can be <code>null</code> if never specified
     * @see #getInitialValue()
     */
    public String getInitialValueNominal() {
        if (initialValue instanceof HLNominalValue) {
            return ((HLNominalValue) getInitialValue()).getValue();
        } else {
            return null;
        }
    }

    /**
     * Adds a new value to the range of possible values.
     * If value has been seen before, it will increase its likelihod attribute
     * compared to the other possible values.
     *
     * @param aValue the value to be added
     */
    public void addPossibleValue(String aValue) {
        getPossibleValues().addPossibleValueNominal(aValue);
    }

    /**
     * Specifies the possible values for the data attribute.
     * <br>
     * If there are duplicate values in the list, their frequency
     * will be captured accordingly.
     *
     * @param values the new possible values for the data attribute
     */
    public void setPossibleValues(List<String> values) {
        getPossibleValues().setPossibleValues(values);
    }

    /*
     * (non-Javadoc)
     * @see org.processmining.framework.models.hlprocess.HLAttribute#isValid(org.processmining.framework.models.hlprocess.att.HLAttributeValue)
     */
    public boolean isValid(HLAttributeValue val) {
        // TODO: also include value range check
        return val instanceof HLNominalValue || val instanceof HLNominalDistribution;
    }

    /*
     * (non-Javadoc)
     * @see org.processmining.framework.models.hlprocess.HLAttribute#getType()
     */
    public AttributeType getType() {
        return AttributeType.Nominal;
    }

}
