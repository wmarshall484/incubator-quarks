package quarks.analytics.sensors;

import java.util.Objects;

import quarks.function.Predicate;

/**
 * A generic "valve" {@link Predicate}.
 * <p>
 * A valve predicate accepts tuples when its state is {@link State#OPEN},
 * otherwise it rejects tuples.
 * </p><p>
 * A valve is typically used to dynamically control whether or not
 * some downstream tuple processing is enabled.  A decision to change the
 * state of the valve may be a result of local analytics or an external
 * command.
 * <br>
 * E.g., a Valve might be used to control whether or not logging
 * of tuples is enabled.
 * <pre>{@code
 * TStream<JsonObject> stream = ...;
 * 
 * Valve<JsonObject> valve = new Valve<>(Valve.State.CLOSED);
 * stream.filter(valve).sink(someTupleLoggingConsumer);
 *                                 
 * // from some analytic or device command handler...
 *     valve.setState(Valve.State.OPEN);
 * }</pre>
 * </p>
 *
 * @param <T> tuple type
 * @see Filters#valve(quarks.topology.TStream, State)
 */
public class Valve<T> implements Predicate<T> {
    private static final long serialVersionUID = 1L;
    private transient State state = State.OPEN;
    
    /**
     * The valve state.
     */
    public enum State { 
        /** accept tuples */ OPEN, /** reject tuples */ CLOSED };

    /**
     * Create a new Valve Predicate
     * <p>
     * Same as {@code Valve(State.OPEN)}
     */
    public Valve() {
        this(State.OPEN);
    }
    
    /**
     * Create a new Valve Predicate
     * <p>
     * @param state the initial {@link State}
     */
    public Valve(State state) {
        setState(state);
    }
    
    /**
     * Set the valve state
     * @param state the state of the valve
     */
    public synchronized void setState(State state) {
        Objects.requireNonNull(state, "state");
        this.state = state;
    }

    @Override
    public boolean test(T value) {
        return state == State.OPEN;
    }

    /**
     * Returns a String for development/debug support.  Content subject to change.
     */
    @Override
    public String toString() {
        return "state="+state;
    }
    
}