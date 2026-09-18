package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.world.IWrappedBlockData;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public final class BlockRegionData extends BlockPosData {
    Vec3i size;

    public BlockRegionData(final Vec3i pos, final Vec3i size, final IWrappedBlockData blockData) {
        super(pos, blockData);
        this.size = size;
    }
}