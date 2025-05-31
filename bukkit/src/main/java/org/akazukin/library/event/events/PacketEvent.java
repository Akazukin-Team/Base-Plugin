package org.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PacketEvent extends Event implements IPacketEvent {
    private static final HandlerList handlers = new HandlerList();
    private final SocketRemoteClient client;
    @Setter
    private Object packet;
    @Setter
    private boolean cancelled = false;

    public PacketEvent(final SocketRemoteClient client, final Object packet) {
        super(true);
        this.client = client;
        this.packet = packet;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
