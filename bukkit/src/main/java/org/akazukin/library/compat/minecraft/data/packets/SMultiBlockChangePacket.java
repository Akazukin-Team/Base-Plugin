package org.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.world.WrappedBlockData;
import org.akazukin.library.worldedit.Vec3;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SMultiBlockChangePacket implements Packet {
    Vec3<Integer> sectionPos;
    BlockInfo[] blockInfo;
    //Object chunkSection;

    @AllArgsConstructor
    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class BlockInfo {
        Vec3<Integer> pos;
        WrappedBlockData blockData;
    }
}
