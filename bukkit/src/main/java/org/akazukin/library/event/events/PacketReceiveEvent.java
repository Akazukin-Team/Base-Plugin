package org.akazukin.library.event.events;

import org.akazukin.library.packetlistener.client.SocketRemoteClient;
import org.bukkit.event.HandlerList;

public class PacketReceiveEvent extends PacketEvent {
    private static final HandlerList handlers = new HandlerList();

    public PacketReceiveEvent(final SocketRemoteClient client, final Object packet) {
        super(client, packet);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
