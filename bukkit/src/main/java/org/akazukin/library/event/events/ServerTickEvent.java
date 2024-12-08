package org.akazukin.library.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerTickEvent extends Event implements IServerTickEvent {
    private static final HandlerList handlers = new HandlerList();

    public ServerTickEvent() {
        super(true);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
