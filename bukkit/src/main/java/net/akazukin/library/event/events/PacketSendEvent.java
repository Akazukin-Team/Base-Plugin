package net.akazukin.library.event.events;

import net.akazukin.library.packetlistener.client.SocketRemoteClient;

public class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(final SocketRemoteClient client, final Object packet) {
        super(client, packet);
    }
}
