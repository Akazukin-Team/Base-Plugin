package net.akazukin.library.packetlistener.client;

import io.netty.channel.Channel;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class UserRemoteClient extends SocketRemoteClient {
    protected UserRemoteClient(final Channel channel) {
        super(channel);
    }

    public abstract UUID getUniqueId();

    public abstract String getName();
}
