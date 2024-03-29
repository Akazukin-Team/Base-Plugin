package net.akazukin.library.event.events;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.event.Event;
import net.akazukin.library.packetlistener.RemoteClient;
import org.bukkit.event.Cancellable;

@Getter
public class PacketEvent extends Event implements Cancellable {
    private final RemoteClient client;
    private final Object packet;
    @Setter
    private boolean cancelled = false;

    public PacketEvent(final RemoteClient client, final Object packet) {
        super(true);
        this.client = client;
        this.packet = packet;
    }
}
