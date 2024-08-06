package net.akazukin.library.event.events;

import net.akazukin.library.event.Event;

public class ServerTickEvent extends Event implements IServerTickEvent {
    public ServerTickEvent() {
        super(true);
    }
}
