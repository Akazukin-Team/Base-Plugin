package net.akazukin.library.packetlistener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public final class RemoteClient {
    private final InetSocketAddress address;

    @Nullable
    private final Player player;
}
