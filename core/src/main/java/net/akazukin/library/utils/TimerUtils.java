package net.akazukin.library.utils;

public class TimerUtils {
    private long start = System.currentTimeMillis();
    private long pauseTime = 0;
    private long pausedTime = 0;

    public long getTimePassed() {
        if (isPaused()) {
            return System.currentTimeMillis() - pausedTime - (System.currentTimeMillis() - pauseTime);
        } else {
            return System.currentTimeMillis() - pausedTime;
        }
    }

    public boolean isPaused() {
        return pauseTime != 0;
    }

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= start + MS;
    }

    public void pause() {
        if (isPaused()) return;
        pauseTime = System.currentTimeMillis();
    }

    public void unpause() {
        pausedTime += System.currentTimeMillis() - pauseTime;
    }

    private void reset() {
        start = System.currentTimeMillis();
        pauseTime = 0;
        pausedTime = 0;
    }
}
