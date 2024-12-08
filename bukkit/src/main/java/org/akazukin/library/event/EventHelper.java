package org.akazukin.library.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class EventHelper {
    public static boolean callAndCheck(final Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event instanceof Cancellable) return !((Cancellable) event).isCancelled();
        return true;
    }
}
