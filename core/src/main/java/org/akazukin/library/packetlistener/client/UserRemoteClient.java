package org.akazukin.library.packetlistener.client;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class UserRemoteClient extends SocketRemoteClient {
    protected UserRemoteClient(final Channel channel) {
        super(channel);
    }

    public abstract UUID getUniqueId();

    public abstract String getName();
}
