package org.akazukin.library.utils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BukkitUtils {
    public static void runTask(final Plugin plugin, final String label, final Runnable runnable) {
        if (!Bukkit.isPrimaryThread()) {
            final AtomicReference<Boolean> ato = new AtomicReference<>();
            final Thread t = new Thread(() -> {
                runnable.run();
                ato.set(Boolean.FALSE);
            }, label);
            Bukkit.getScheduler().runTask(plugin, t);
            try {
                t.join();
                while (ato.get() == null) Thread.sleep(10);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else runnable.run();
    }

    public static <T> T runTask(final Plugin plugin, final String label, final Supplier<T> runnable) {
        if (!Bukkit.isPrimaryThread()) {
            final AtomicReference<T> ato = new AtomicReference<>();
            final Thread t = new Thread(() -> ato.set(runnable.get()), label);
            Bukkit.getScheduler().runTask(plugin, t);
            try {
                t.join();

                while (ato.get() == null) Thread.sleep(10);
                return ato.get();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else return runnable.get();
    }
}
