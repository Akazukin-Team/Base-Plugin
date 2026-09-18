package org.akazukin.library.compat.minecraft.compats.v1_20_R1;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
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
        if (!(o instanceof final WrappedBlockData that)) {
            return false;
        }
        return Objects.equals(this.blockData, that.blockData);
    }

    @Override
    public WrappedBlockState toPacketBlockState() {
        final int id = Block.i(this.blockData);
        return WrappedBlockState.getByGlobalId(id);
    }
}
