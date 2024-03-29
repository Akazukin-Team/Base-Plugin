package net.akazukin.library.packetlistener;

import io.netty.channel.Channel;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class PacketManager implements Listenable {
    @EventTarget
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final Channel channel = LibraryPlugin.COMPAT.getPlayerChannel(player);
        if (channel == null) {
            LibraryPlugin.getLogManager().severe("Couldn't inject packet handler to " + player.getName());
        } else {
            InjectionUtils.injectCustomHandler(player, channel);
        }
    }

    @EventTarget
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Channel channel = LibraryPlugin.COMPAT.getPlayerChannel(player);
        if (channel == null) {
            LibraryPlugin.getLogManager().severe("Couldn't inject packet handler to " + player.getName());
        } else {
            InjectionUtils.removeCustomHandler(channel);
        }
    }

    @EventTarget
    public void onServerListPing(final ServerListPingEvent event) {
        final List<Channel> channels = LibraryPlugin.COMPAT.getServerChannels();

        if (channels == null) {
            LibraryPlugin.getLogManager().warning("Couldn't get active server's channels !");
        } else {
            for (final Channel channel : channels) {
                InjectionUtils.injectCustomHandler(null, channel);
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}