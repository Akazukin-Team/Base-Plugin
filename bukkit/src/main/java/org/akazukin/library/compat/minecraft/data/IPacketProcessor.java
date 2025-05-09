package org.akazukin.library.compat.minecraft.data;

import org.akazukin.library.compat.minecraft.data.packets.Packet;

public interface IPacketProcessor<T> {
    T processWrapper(final Packet packet) throws NoSuchFieldException, IllegalAccessException;

    Packet processPacket(final T packet);
}
