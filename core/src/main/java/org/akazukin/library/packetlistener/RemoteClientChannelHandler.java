package org.akazukin.library.packetlistener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.logging.Level;
import org.akazukin.library.LibraryPluginProvider;
import org.akazukin.library.event.events.IPacketEvent;
import org.akazukin.library.packetlistener.client.SocketRemoteClient;

/**
 * Custom channel handler.
 *
 * <p>
 * This channel handler is injected into player's channel and fire
 * {@code PacketEvent} depends on if client send data or server send data.
 * </p>
 *
 * @author DrogoniEntity
 * @see IPacketEvent class.
 */
public abstract class RemoteClientChannelHandler extends ChannelDuplexHandler {
    /**
     * Channel handler name.
     */
    public static final String HANDLER_NAME = "akz_handler";

    /**
     * Remote client who communicate with server.
     */
    private final SocketRemoteClient client;

    /**
     * Setup channel handler.
     *
     * @param client - remote client.
     */
    public RemoteClientChannelHandler(final SocketRemoteClient client) {
        this.client = client;
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        final Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, EventType.SEND)) {
            super.write(ctx, msgPointer.content, promise);
        }
    }

    /**
     * Custom job to execute before continuing netty's job.
     *
     * <p>
     * If {@code msg} is a valide Minecraft packet, a new packet event will be
     * created and fired by Bukkit's plugin manager.
     * </p>
     *
     * @param msg       - data to handle.
     * @param eventType - {@code PacketEvent} type to fire.
     * @return {@code true} if event havn't been cancelled.
     */
    private boolean handle(final Pointer<Object> msg, final EventType eventType) {
        boolean shouldContinue = true;

        // Proceed only if msg's class name contains 'net.minecraft' and 'Packet'.
        final String className = msg.content.getClass().getName();
        if (className.contains("net.minecraft") && className.contains("Packet")) {
            try {
                final IPacketEvent event = this.getEventClass(eventType)
                        .getConstructor(SocketRemoteClient.class, Object.class)
                        .newInstance(this.client, msg.content);
                this.firePktEvent(event);
                msg.content = event.getPacket();

                shouldContinue = !event.isCancelled();
            } catch (final Throwable ignored) {
                LibraryPluginProvider.getApi().getLogManager().log(Level.SEVERE,
                        "An error has occurred when handle packet", ignored);
            }
        }

        return shouldContinue;
    }

    protected abstract Class<? extends IPacketEvent> getEventClass(EventType type);

    protected abstract void firePktEvent(IPacketEvent event);

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, EventType.RECEIVE)) {
            super.channelRead(ctx, msgPointer.content);
        }
    }

    protected enum EventType {
        SEND,
        RECEIVE
    }
}
