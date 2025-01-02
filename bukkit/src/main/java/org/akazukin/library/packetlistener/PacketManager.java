package org.akazukin.library.packetlistener;

import io.netty.channel.Channel;
import java.util.List;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.packetlistener.client.BukkitRemoteClient;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class PacketManager implements Listenable {
    @EventTarget(libraryPriority = 3)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final Channel channel = LibraryPlugin.getPlugin().getCompat().getPlayerChannel(player);
        if (channel == null) {
            LibraryPlugin.getPlugin().getLogManager().severe("Couldn't inject packet handler to " + player.getName());
        } else {
            LibraryPlugin.getPlugin().getInjectionManager().injectCustomHandler(new BukkitRemoteClient(player,
                    channel));
        }
    }

    @EventTarget
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Channel channel = LibraryPlugin.getPlugin().getCompat().getPlayerChannel(player);
        if (channel == null) {
            LibraryPlugin.getPlugin().getLogManager().severe("Couldn't inject packet handler to " + player.getName());
        } else {
            LibraryPlugin.getPlugin().getInjectionManager().removeCustomHandler(new SocketRemoteClient(channel));
        }
    }

    @EventTarget(libraryPriority = 3)
    public void onServerListPing(final ServerListPingEvent event) {
        final List<Channel> channels = LibraryPlugin.getPlugin().getCompat().getServerChannels();

        if (channels == null) {
            LibraryPlugin.getPlugin().getLogManager().warning("Couldn't get active server's channels !");
        } else {
            for (final Channel channel : channels) {
                LibraryPlugin.getPlugin().getInjectionManager().injectCustomHandler(new SocketRemoteClient(channel));
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}