package net.akazukin.library.event;

import org.bukkit.event.HandlerList;

public abstract class Event extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Event() {
        super(false);
    }

    public Event(final boolean async) {
        super(async);
    }
}
