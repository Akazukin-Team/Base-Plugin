package org.akazukin.library.packetlistener.client;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class SocketRemoteClient {
    private final Channel channel;

    public InetSocketAddress getAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }
}
