package org.akazukin.library.event.events;

import org.akazukin.event.ICancellableEvent;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;

public interface IPacketEvent extends ICancellableEvent {
    SocketRemoteClient getClient();

    Object getPacket();
}