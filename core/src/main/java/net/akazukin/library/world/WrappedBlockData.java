package net.akazukin.library.world;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WrappedBlockData {
    Object blockData;
    int blockId;
    byte data;

    public WrappedBlockData(final Object blockState) {
        this.blockData = blockState;
        /*((IBlockState)(blockState));
        ((BlockState)blockState);
        ((CraftBlock)block).getNMS();
        new CraftBlock()*/
        this.blockId = -1;
        this.data = -1;
    }

    public WrappedBlockData(final int blockId, final byte data) {
        this.blockData = null;
        this.blockId = blockId;
        this.data = data;
    }

    public boolean isLegacy() {
        return this.blockData == null;
    }
}
