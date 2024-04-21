package net.akazukin.library.packetlistener;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.akazukin.library.event.events.PacketEvent;
import net.akazukin.library.event.events.PacketReceiveEvent;
import net.akazukin.library.event.events.PacketSendEvent;
import org.bukkit.Bukkit;

/**
 * Custom channel handler.
 *
 * <p>
 * This channel handler is injected into player's channel and fire
 * {@code PacketEvent} depends on if client send data or server send data.
 * </p>
 *
 * @author DrogoniEntity
 * @see PacketEvent PacketEvent class.
 */
public class RemoteClientChannelHandler extends ChannelDuplexHandler {
    /**
     * Channel handler name.
     */
    public static final String HANDLER_NAME = "akz_prehandler";

    /**
     * Remote client who communicate with server.
     */
    private final RemoteClient client;

    /**
     * Setup channel handler.
     *
     * @param client - remote client.
     */
    public RemoteClientChannelHandler(final RemoteClient client) {
        this.client = client;
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        final Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, PacketSendEvent.class))
            super.write(ctx, msgPointer.content, promise);
    }

    /**
     * Custom job to execute before continuing netty's job.
     *
     * <p>
     * If {@code msg} is a valide Minecraft packet, a new packet event will be
     * created and fired by Bukkit's plugin manager.
     * </p>
     *
     * @param msg        - data to handle.
     * @param eventClass - {@code PacketEvent} type to fire.
     * @return {@code true} if event havn't been cancelled.
     */
    private boolean handle(final Pointer<Object> msg, final Class<? extends PacketEvent> eventClass) {
        boolean shouldContinue = true;

        // Proceed only if msg's class name contains 'net.minecraft' and 'Packet'.
        final String className = msg.content.getClass().getName();
        if (className.contains("net.minecraft") && className.contains("Packet")) {
            try {
                final PacketEvent event = eventClass.getConstructor(RemoteClient.class, Object.class).newInstance(this.client, msg.content);
                Bukkit.getServer().getPluginManager().callEvent(event);
                msg.content = event.getPacket();

                shouldContinue = !event.isCancelled();
            } catch (final Throwable ignored) {
                ignored.printStackTrace();
            }
        }

        return shouldContinue;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final Pointer<Object> msgPointer = new Pointer<>(msg);
        if (this.handle(msgPointer, PacketReceiveEvent.class))
            super.channelRead(ctx, msgPointer.content);
    }
}
