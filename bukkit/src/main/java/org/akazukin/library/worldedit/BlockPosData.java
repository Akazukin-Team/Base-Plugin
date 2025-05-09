package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.world.WrappedBlockData;

import java.util.Objects;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class BlockPosData {
    Vec3<Integer> pos;
    WrappedBlockData blockData;

    @Override
    public int hashCode() {
        return Objects.hash(this.pos);
    }
}
