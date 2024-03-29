package net.akazukin.library.event.events;

import net.akazukin.library.packetlistener.RemoteClient;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(final RemoteClient client, final Object packet) {
        super(client, packet);
    }
}
