package org.akazukin.library.packetlistener.client;

import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocketRemoteClient {
    private final Channel channel;

    public InetSocketAddress getAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }
}
