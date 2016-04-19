package quarks.analytics.sensors;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import quarks.function.Predicate;

/**
 * A generic "deadtime" {@link Predicate}.
 * <p>
 * After accepting a tuple ({@link #test(Object) test()} returns true),
 * any tuples received during the "deadtime" period are rejected
 * ({@link #test(Object) test()} returns false).
 * Then the next tuple is accepted and a new deadtime period begun.
 * </p><p>
 * The deadtime period may be changed while the topology is running
 * via {@link #setPeriod(long, TimeUnit)}.
 * </p>
 *
 * @param <T> tuple type
 */
public class Deadtime<T> implements Predicate<T> {
    private static final long serialVersionUID = 1L;
    private transient long deadtimePeriod;
    private transient TimeUnit deadtimeUnit;
    private transient long deadtimePeriodMillis;
    private transient long lastPassTimeMillis;
    private transient long nextPassTimeMillis;

    /**
     * Create a new deadtime Predicate
     * <p>
     * Same as {@code Deadtime(0, TimeUnit.SECONDS)}
     */
    public Deadtime() {
        setPeriod(0, TimeUnit.SECONDS);
    }
    
    /**
     * Create a new deadtime Predicate
     * <p>
     * The first received tuple is always "accepted".
     * @param deadtimePeriod see {@link #setPeriod(long, TimeUnit) setDeadtimePeriod()}
     * @param unit {@link TimeUnit} of {@code deadtimePeriod}
     */
    public Deadtime(long deadtimePeriod, TimeUnit unit) {
        setPeriod(deadtimePeriod, unit);
    }
    
    /**
     * Set the deadtime period
     * <p>
     * The next time to enable a tuple to be accepted is
     * immediately adjusted relative to the last accepted tuple time.
     * </p><p>
     * The deadtime period behavior is subject to the accuracy
     * of the system's {@link System#currentTimeMillis()}.
     * </p>
     * @param deadtimePeriod the amount of to time to reject
     *        tuples received after the last passed tuple.
     *        Specify a value of 0 to pass all received tuples.
     *        Must be >= 0.
     *        A period of 0 is used if the specified period is less than 1ms.
     * @param unit {@link TimeUnit} of {@code deadtimePeriod}
     */
    public synchronized void setPeriod(long deadtimePeriod, TimeUnit unit) {
        if (deadtimePeriod < 0)
            throw new IllegalArgumentException("deadtimePeriod");
        Objects.requireNonNull(unit, "unit");
        this.deadtimePeriod = deadtimePeriod;
        this.deadtimeUnit = unit;
        this.deadtimePeriodMillis = unit.toMillis(deadtimePeriod);
        nextPassTimeMillis = lastPassTimeMillis + deadtimePeriodMillis;
    }

    @Override
    public boolean test(T value) {
        long now = System.currentTimeMillis(); 
        if (now < nextPassTimeMillis)
            return false;
        else {
            lastPassTimeMillis = now;
            nextPassTimeMillis = lastPassTimeMillis + deadtimePeriodMillis;
            return true;
        }
    }

    /**
     * Returns a String for development/debug support.  Content subject to change.
     */
    @Override
    public String toString() {
        return "deadtimePeriod="+deadtimePeriod+" "+deadtimeUnit 
                + " nextPass after "+new Date(nextPassTimeMillis);
    }
    
}