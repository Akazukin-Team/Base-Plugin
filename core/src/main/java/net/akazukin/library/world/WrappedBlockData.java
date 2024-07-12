package net.akazukin.library.world;

import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WrappedBlockData {
    Object blockData;
    int blockId;
    byte data;

    public WrappedBlockData(@Nonnull final Object blockState) {
        this.blockData = blockState;
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
