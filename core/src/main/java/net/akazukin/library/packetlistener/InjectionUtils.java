package net.akazukin.library.packetlistener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import net.akazukin.library.LibraryPlugin;
import org.bukkit.entity.Player;

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
        LibraryPlugin.getLogManager().log(Level.OFF, "Injecting connection channel handler to " + (player == null ? channel.remoteAddress() : player.getName()));

        final ChannelPipeline pipeline = channel.pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains("packet_handler") && !registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME)) {
            final RemoteClient client = new RemoteClient((InetSocketAddress) channel.remoteAddress(), player);
            final RemoteClientChannelHandler handler = new RemoteClientChannelHandler(client);
            pipeline.addBefore("packet_handler", RemoteClientChannelHandler.HANDLER_NAME, handler);
        }

        LibraryPlugin.getLogManager().log(Level.OFF, "Successfully Injected connection channel handler to " + (player == null ? channel.remoteAddress() : player.getName()));
    }

    public static void removeCustomHandler(@Nonnull final Channel channel) {
        LibraryPlugin.getLogManager().log(Level.OFF, "Rejecting connection channel handler of " + channel.remoteAddress());

        final ChannelPipeline pipeline = channel.pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME))
            channel.pipeline().remove(RemoteClientChannelHandler.HANDLER_NAME);

        LibraryPlugin.getLogManager().log(Level.OFF, "Successfully rejected connection channel handler of " + channel.remoteAddress());
    }
}
