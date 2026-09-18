package org.akazukin.library.world;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface IWrappedBlockData {
    WrappedBlockState toPacketBlockState();
}
