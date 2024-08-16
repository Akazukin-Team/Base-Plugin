package net.akazukin.library.utils;

public final class TimerUtils {
    /**
     * The Time.
     */
    private long startedTime = -1L;
    private long pausedAtTime = -1L;

    public TimerUtils() {
        this.reset();
    }

    /**
     * Reset.
     */
    public void reset() {
        this.startedTime = System.nanoTime();
        this.pausedAtTime = -1L;
    }

    public long getPassedSecTime() {
        return this.getPassedMSTime() / 1000L;
    }

    public long getPassedMSTime() {
        return this.getPassedNanoTime() / 1000000L;
    }

    public long getPassedNanoTime() {
        return System.nanoTime() - this.startedTime -
                (this.pausedAtTime != -1 ? System.nanoTime() - this.pausedAtTime : 0);
    }

    public void setPassedNanoTime(final long nano) {
        this.pausedAtTime = -1;
        this.startedTime = System.nanoTime() - nano;
    }

    public void setPassedMSTime(final long ms) {
        this.setPassedNanoTime(ms * 1000000L);
    }

    public void setPassedSecTime(final long sec) {
        this.setPassedMSTime(sec * 1000L);
    }

    /**
     * Has time passed boolean.
     *
     * @param sec the seconds
     * @return the boolean
     */
    public boolean hasSecTimePassed(final long sec) {
        return this.hasMSTimePassed(sec * 1000L);
    }

    /**
     * Has time passed boolean.
     *
     * @param ms the milliseconds
     * @return the boolean
     */
    public boolean hasMSTimePassed(final long ms) {
        return this.hasNanoTimePassed(ms * 1000000L);
    }

    /**
     * Has time passed boolean.
     *
     * @param nano the nanoseconds
     * @return the boolean
     */
    public boolean hasNanoTimePassed(final long nano) {
        return System.nanoTime() >= this.startedTime + nano +
                (this.pausedAtTime != -1L ? System.nanoTime() - this.pausedAtTime : 0);
    }

    /**
     * Has time left long.
     *
     * @param sec the seconds
     * @return the long
     */
    public long hasSecTimeLeft(final long sec) {
        return this.hasMSTimeLeft(sec * 1000L);
    }

    /**
     * Has time left long.
     *
     * @param ms the milliseconds
     * @return the long
     */
    public long hasMSTimeLeft(final long ms) {
        return this.hasNanoTimeLeft(ms * 1000000L);
    }

    /**
     * Has time left long.
     *
     * @param nano the nanoseconds
     * @return the long
     */
    public long hasNanoTimeLeft(final long nano) {
        return (nano + this.startedTime) - System.nanoTime();
    }

    /**
     * pause the timer
     */
    public void pause() {
        if (this.pausedAtTime == -1L) this.pausedAtTime = System.nanoTime();
    }

    /**
     * resume the timer
     */
    public void resume() {
        if (this.pausedAtTime == -1L) return;

        this.startedTime += System.nanoTime() - this.pausedAtTime;
        this.pausedAtTime = -1L;
    }

    public boolean isPaused() {
        return this.pausedAtTime != -1L;
    }
}