package org.akazukin.library.packetlistener;

import org.akazukin.library.event.events.IPacketEvent;
import org.akazukin.library.event.events.PacketEvent;
import org.akazukin.library.event.events.PacketReceiveEvent;
import org.akazukin.library.event.events.PacketSendEvent;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class BukkitChannelHandler extends RemoteClientChannelHandler {
    /**
     * Setup channel handler.
     *
     * @param client - remote client.
     */
    public BukkitChannelHandler(final SocketRemoteClient client) {
        super(client);
    }

    @Override
    protected Class<? extends PacketEvent> getEventClass(final EventType type) {
        return type == EventType.RECEIVE ? PacketReceiveEvent.class : PacketSendEvent.class;
    }

    @Override
    protected void firePktEvent(final IPacketEvent event) {
        Bukkit.getPluginManager().callEvent((Event) event);
    }
}
