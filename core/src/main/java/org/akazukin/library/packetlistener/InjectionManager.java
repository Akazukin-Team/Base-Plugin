package org.akazukin.library.packetlistener;

import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.LibraryPluginProvider;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;
import org.akazukin.library.packetlistener.client.UserRemoteClient;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class InjectionManager {
    Class<? extends RemoteClientChannelHandler> channelHandler;

    public void injectCustomHandler(final SocketRemoteClient client) {
        LibraryPluginProvider.getApi().getLogManager().log(Level.OFF,
                "Injecting connection channel handler to " +
                        (client instanceof UserRemoteClient ?
                                ((UserRemoteClient) client).getName() : client.getAddress()));

        final ChannelPipeline pipeline = client.getChannel().pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains("packet_handler") && !registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME)) {
            final RemoteClientChannelHandler handler;
            try {
                handler = this.channelHandler.getConstructor(SocketRemoteClient.class).newInstance(client);
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                           NoSuchMethodException e) {
                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE, "Failed to inject packet handler", e);
                return;
            }
            pipeline.addBefore("packet_handler", RemoteClientChannelHandler.HANDLER_NAME, handler);
        }

        LibraryPluginProvider.getApi().getLogManager().log(Level.OFF,
                "Successfully Injected connection channel handler to " +
                        (client instanceof UserRemoteClient ?
                                ((UserRemoteClient) client).getName() : client.getAddress()));
    }

    public void removeCustomHandler(@NonNull final SocketRemoteClient client) {
        LibraryPluginProvider.getApi().getLogManager().log(Level.OFF,
                "Rejecting connection channel handler of " +
                        (client instanceof UserRemoteClient ?
                                ((UserRemoteClient) client).getName() : client.getAddress()));

        final ChannelPipeline pipeline = client.getChannel().pipeline();
        final List<String> registeredHandlers = pipeline.names();

        if (registeredHandlers.contains(RemoteClientChannelHandler.HANDLER_NAME)) {
            pipeline.remove(RemoteClientChannelHandler.HANDLER_NAME);
        }

        LibraryPluginProvider.getApi().getLogManager().log(Level.OFF,
                "Successfully rejected connection channel handler of " +
                        (client instanceof UserRemoteClient ?
                                ((UserRemoteClient) client).getName() : client.getAddress()));
    }
}
