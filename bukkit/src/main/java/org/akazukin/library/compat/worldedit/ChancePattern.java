package org.akazukin.library.compat.worldedit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.data.BlockData;

@Getter
@Setter
public class ChancePattern {
    private final BlockData blockData;
    private final double chance;

    public ChancePattern(final BlockData blockData, final double chance) {
        this.blockData = blockData;
        this.chance = chance;
    }
}
