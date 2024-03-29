package net.akazukin.library.packetlistener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Utility used to perform custom channel handler injection.
 *
 * <p>
 * This tool allow to retreive active channels and to inject custom channel
 * handler. That's all.
 * <p>
 *
 * @author DrogoniEntity
 */
public class InjectionUtils {
    public static void injectCustomHandler(final Player player, @Nonnull final Channel channel) {
        LibraryPlugin.getLogManager().info("Injecting connection channel handler to " + player.getName());

        final ChannelPipeline pipeline = channel.pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains("packet_handler") && !registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME)) {
            final RemoteClient client = new RemoteClient((InetSocketAddress) channel.remoteAddress(), player);
            final RemoteClientChannelHandler handler = new RemoteClientChannelHandler(client);
            pipeline.addBefore("packet_handler", RemoteClientChannelHandler.HANDLER_NAME, handler);
        }

        LibraryPlugin.getLogManager().info("Successfully Injected connection channel handler to " + player.getName());
    }

    public static void removeCustomHandler(@Nonnull final Channel channel) {
        LibraryPlugin.getLogManager().info("Rejecting connection channel handler");

        final ChannelPipeline pipeline = channel.pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME))
            channel.pipeline().remove(RemoteClientChannelHandler.HANDLER_NAME);

        LibraryPlugin.getLogManager().info("Successfully rejected connection channel handler");
    }
}
