package org.akazukin.library.worldedit;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface IBlockPosData {
    Vec3i getPos();

    WrappedBlockState getPacketBlockState();
}
