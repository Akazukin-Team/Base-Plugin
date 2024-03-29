package net.akazukin.library.compat.minecraft.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WrappedBlockPos {
    private int x;
    private int y;
    private int z;

    public WrappedBlockPos(final Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }
}
