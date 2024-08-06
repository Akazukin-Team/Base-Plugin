package net.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.Event;
import net.akazukin.library.packetlistener.client.SocketRemoteClient;

@Getter
public class PacketEvent extends Event implements IPacketEvent {
    private final SocketRemoteClient client;
    private final Object packet;
    @Setter
    private boolean cancelled = false;

    public PacketEvent(final SocketRemoteClient client, final Object packet) {
        super(true);
        this.client = client;
        this.packet = packet;
    }
}
