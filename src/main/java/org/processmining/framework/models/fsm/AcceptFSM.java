package org.processmining.framework.models.fsm;

import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphVertex;

import java.util.HashSet;

/**
 * <p>Title: AcceptFSM</p>
 * <p>
 * <p>Description: Models an Accept(ing) Finite State Machine.</p>
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
public class AcceptFSM extends FSM {

    /**
     * Start (or initial) state.
     */
    private FSMState startState;
    private HashSet<FSMState> startStates;

    /**
     * Accepting states.
     */
    private HashSet<FSMState> acceptStates;

    /**
     * Create an Accept FSM with given name.
     *
     * @param name String The given name.
     */
    public AcceptFSM(String name) {
        super(name);
        startState = null;
        startStates = new HashSet<FSMState>();
        acceptStates = new HashSet<FSMState>();
    }

    /**
     * Returns the current start state.
     *
     * @return FSMState The current start state. Could be null (if no start state has been set).
     */
    public FSMState getStartState() {
        return startState;
    }

    /**
     * Set the start state for this Accept FSM to the given state.
     *
     * @param state FSMState The given state.
     */
    public void setStartState(FSMState state) {
        if (startState == null) {
            startState = state;
            startStates.add(state);
            /**
             * Add an invisible vertex with an edge from that node to the start state.
             */
            FSM fsm = (FSM) state.getGraph();
            ModelGraphVertex vertex = new ModelGraphVertex(fsm);
            vertex.setDotAttribute("style", "invis");
            vertex.setDotAttribute("label", "");
            fsm.addDummy(vertex);
            ModelGraphEdge edge = new ModelGraphEdge(vertex, state);
            fsm.addDummy(edge);
        } else if (startState != state) {
            /*
             * Another start state. Add (silent) transition from the first state state.
             */
            FSMState proxyState = getState(state.getPayload());
            if (proxyState == null) {
                proxyState = state;
            }
            if (!startStates.contains(proxyState)) {
                startStates.add(proxyState);
                FSMTransition transition = addTransition(startState, proxyState, "switch start state");
                transition.setDotAttribute("constraint", "false");
                transition.setDotAttribute("color", "blue");
            }
        }
    }

    /**
     * Returns all start states.
     *
     * @return HashSet<FSMState> The set of all start states. Not null.
     */
    public HashSet<FSMState> getStartStates() {
        return startStates;
    }

    /**
     * Add the given state to the set of accepting states.
     *
     * @param state FSMState The given state.
     */
    public void addAcceptState(FSMState state) {
        acceptStates.add(state);
        state.setDotAttribute("peripheries", "2");
    }

    /**
     * Remove the given state from the set of accepting states.
     *
     * @param state FSMState
     */
    public void removeAcceptState(FSMState state) {
        acceptStates.remove(state);
        state.clearDotAttribute("peripheries");
    }

    /**
     * Clear the set of accepting states.
     */
    public void clearAcceptState() {
        for (FSMState state : acceptStates) {
            state.clearDotAttribute("peripheries");
        }
        acceptStates = new HashSet<FSMState>();
    }

    /**
     * Returns the set of accepting states.
     *
     * @return HashSet<FSMState> The set of accepting states. Not null.
     */
    public HashSet<FSMState> getAcceptStates() {
        return acceptStates;
    }

}
