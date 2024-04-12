package net.akazukin.library.managers;

import net.akazukin.library.utils.TaskUtils;

public class TaskManager {
    public synchronized static void runLoop() {
        try {
            while (true) {
                synchronized (TaskUtils.tasks) {
                    if (!TaskUtils.tasks.isEmpty())
                        TaskUtils.tasks.poll().run();
                }
            }
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }
}
