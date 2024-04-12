package net.akazukin.library.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public class TaskUtils {

    public static final Deque<Runnable> tasks = new ArrayDeque<>();

    public static void addSynchronizedTask(final Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    public static <R> R addSynchronizedTask(final Supplier<R> task) {
        synchronized (tasks) {
            return task.get();
        }
    }
}
