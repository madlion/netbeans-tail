package de.fishheadsoftware.netbeanstail;

/**
 *
 * @author madlion
 */
public interface ScheduleListener {
    
    void cancelled(final TailRunnable runnable);
}
