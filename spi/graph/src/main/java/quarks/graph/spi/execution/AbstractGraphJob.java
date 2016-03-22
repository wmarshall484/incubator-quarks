/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2015  
*/
package quarks.graph.spi.execution;

import quarks.execution.Job;

/**
 * Placeholder for a skeletal implementation of the {@link Job} interface,
 * to minimize the effort required to implement the interface.
 */
public abstract class AbstractGraphJob implements Job {
    private State currentState;
    private State nextState;
    private Health health;
    private String lastError;

    protected AbstractGraphJob() {
        this.currentState = State.CONSTRUCTED;
        this.nextState = currentState;
        this.health = Health.HEALTHY;
        this.lastError = new String();
    }

    @Override
    public synchronized State getCurrentState() {
        return currentState;
    }

    @Override
    public synchronized State getNextState() {
        return nextState;
    }

    @Override
    public abstract void stateChange(Action action);
    
    @Override
    public Health getHealth() {
        return health;
    }

    @Override
    public String getLastError() {
        return lastError;
    }

    protected synchronized boolean inTransition() {
        return getNextState() != getCurrentState();
    }

    protected synchronized void setNextState(State value) {
        this.nextState = value;
    }
    
    protected synchronized void completeTransition() {
        if (inTransition()) {
            currentState = nextState;
        }
    }
    
    protected void setHealth(Health value) {
        this.health = value;
    }
    
    protected void setLastError(String value) {
        this.lastError = value;
    }
}
