package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.world.WrappedBlockData;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public final class BlockRegionData extends BlockPosData {
    Vec3<Integer> size;

    public BlockRegionData(final Vec3<Integer> pos, final Vec3<Integer> size, final WrappedBlockData blockData) {
        super(pos, blockData);
        this.size = size;
    }
}