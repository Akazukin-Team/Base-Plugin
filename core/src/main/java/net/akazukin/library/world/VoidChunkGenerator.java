package net.akazukin.library.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {
    @Override
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.0D, 1.0D, 0.0D);
    }
}
