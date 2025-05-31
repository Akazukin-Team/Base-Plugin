package org.akazukin.library.world;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nonnull;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(this.blockData, this.blockId, this.data);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final WrappedBlockData that = (WrappedBlockData) o;
        if (this.isLegacy() != that.isLegacy()) {
            return false;
        }
        return !this.isLegacy() ?
                Objects.equals(this.blockData, that.blockData) :
                this.blockId == that.blockId && this.data == that.data;
    }

    public boolean isLegacy() {
        return this.blockData == null;
    }
}
