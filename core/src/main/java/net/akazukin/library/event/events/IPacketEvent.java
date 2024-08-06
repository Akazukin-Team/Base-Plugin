package net.akazukin.library.event.events;

import net.akazukin.library.event.ICancellableEvent;
import net.akazukin.library.packetlistener.client.SocketRemoteClient;

public interface IPacketEvent extends ICancellableEvent {
    SocketRemoteClient getClient();

    Object getPacket();
}