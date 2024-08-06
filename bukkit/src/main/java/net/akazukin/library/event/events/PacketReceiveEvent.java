package net.akazukin.library.event.events;

import net.akazukin.library.packetlistener.client.SocketRemoteClient;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(final SocketRemoteClient client, final Object packet) {
        super(client, packet);
    }
}
