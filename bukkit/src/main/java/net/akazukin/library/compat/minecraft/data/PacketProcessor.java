package net.akazukin.library.compat.minecraft.data;

import net.akazukin.library.compat.minecraft.data.packets.Packet;

public interface PacketProcessor<T> {
    T processWrapper(final Packet packet) throws NoSuchFieldException, IllegalAccessException;

    Packet processPacket(final T packet);
}
