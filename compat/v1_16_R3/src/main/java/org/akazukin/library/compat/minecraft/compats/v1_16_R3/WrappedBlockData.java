package org.akazukin.library.compat.minecraft.compats.v1_16_R3;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.akazukin.library.world.IWrappedBlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WrappedBlockData implements IWrappedBlockData {
    IBlockData blockData;

    public WrappedBlockData(@NotNull final IBlockData blockState) {
        this.blockData = blockState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.blockData);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedBlockData)) {
            return false;
        }
        final WrappedBlockData that = (WrappedBlockData) o;
        return Objects.equals(this.blockData, that.blockData);
    }

    @Override
    public WrappedBlockState toPacketBlockState() {
        final int id = Block.getCombinedId(this.blockData);
        return WrappedBlockState.getByGlobalId(id);
    }
}
