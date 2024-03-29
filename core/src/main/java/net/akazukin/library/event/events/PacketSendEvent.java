package net.akazukin.library.event.events;

import net.akazukin.library.packetlistener.RemoteClient;

public class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(final RemoteClient client, final Object packet) {
        super(client, packet);
    }
}
