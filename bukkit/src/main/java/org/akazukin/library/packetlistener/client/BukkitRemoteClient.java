package org.akazukin.library.packetlistener.client;

import io.netty.channel.Channel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class BukkitRemoteClient extends UserRemoteClient {
    private final Player player;

    public BukkitRemoteClient(final Player player, final Channel channel) {
        super(channel);
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.player != null ? this.player.getUniqueId() : null;
    }

    @Override
    public String getName() {
        return this.player != null ? this.player.getName() : null;
    }
}
