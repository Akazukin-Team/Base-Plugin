package net.akazukin.library.packetlistener;

import java.net.InetSocketAddress;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public final class RemoteClient {
    private final InetSocketAddress address;

    @Nullable
    private final Player player;
}
