package org.akazukin.library.worldedit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.ICompat;
import org.akazukin.library.utils.LambdaUtils2;
import org.akazukin.library.utils.WorldUtils;
import org.akazukin.library.world.IWrappedBlockData;
import org.akazukin.util.utils.LambdaUtils;
import org.akazukin.util.utils.MathUtils;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EditSession {
    public static final String DIMENSIONS_MUST_BE_POSITIVE = "Any dimension of the size must be positive";
    public static final BlockPosData[] EMPTY_BLOCK_POS_DATA = new BlockPosData[0];
    private final Set<BlockPosData> posData = new HashSet<>();
    private final int threads;
    private final World world;

    public void setBlock(final Vec3i loc, final IWrappedBlockData state) {
        this.setBlock(new BlockPosData(loc, state));
    }

    /**
     * Assigns the specified block position data to the necessary structure.
     * The position and block information are encapsulated in the provided {@link BlockPosData} object.
     *
     * @param posData The block position data that contains the position (3D coordinates) and block state.
     */
    public void setBlock(final BlockPosData posData) {
        this.setBlockInternal(posData);
    }

    private synchronized void setBlockInternal(final BlockPosData posData) {
        this.posData.add(posData);
    }

    /**
     * Sets a block or a region of blocks in the target space defined by the given positions and block state.
     * The method determines the minimum and maximum boundaries from the two position vectors,
     * calculates the size of the region, and applies the given block state across the region.
     *
     * @param pos   The first corner position of the block or region, represented as a 3D vector.
     * @param pos2  The second corner position of the block or region, represented as a 3D vector.
     * @param state The block state to be applied, encapsulating block data, ID, and any metadata.
     */
    public void setBlock(final Vec3i pos, final Vec3i pos2, final IWrappedBlockData state) {
        final Vec3i minPos = new Vec3i(
                Math.min(pos.getX(), pos2.getX()),
                Math.min(pos.getY(), pos2.getY()),
                Math.min(pos.getZ(), pos2.getZ())
        );
        final Vec3i size = new Vec3i(
                Math.max(pos.getX(), pos2.getX()),
                Math.max(pos.getY(), pos2.getY()),
                Math.max(pos.getZ(), pos2.getZ()))
                .minus(minPos);

        this.setBlock(new BlockRegionData(minPos, size, state));
    }

    /**
     * Sets a block or a region of blocks at the specified position using the provided block region data object.
     * If the size of the region contains negative dimensions, the method normalizes those dimensions
     * by adjusting the position accordingly and recalculating the boundaries.
     * Delegates the internal processing to `setBlockInternal`.
     *
     * @param posData The block region data that contains the starting position, dimensions (size),
     *                and block state to be applied across the specified area.
     */
    public void setBlock(final BlockRegionData posData) {
        if (posData.getSize().getX() <= 0
                && posData.getSize().getY() <= 0
                && posData.getSize().getZ() <= 0) {
            throw new IllegalArgumentException(DIMENSIONS_MUST_BE_POSITIVE);
        }
        this.setBlockInternal(posData);
    }

    public void queue() {
        this.queue(null);
    }

    public void queue(final Runnable finishSession) {
        new Thread(() -> {
            this.complete();
            if (finishSession != null) {
                finishSession.run();
            }
        }, "EditSession-AkzLibs").start();
    }

    @SneakyThrows
    public synchronized void complete() {
        System.out.println("Preinitializing EditSession");

        final ICompat compat = LibraryPlugin.getPlugin().getCompat();

        final Object w = compat.getNMSWorld(this.world);
        final int maxY = this.world.getMaxHeight();
        final int minY = compat.getMinHeight(this.world);

        final ExecutorService pool = Executors.newFixedThreadPool(this.threads);

        final long[] chunks = pool.invokeAll(
                        LambdaUtils.toCallable(this.posData, d -> {
                            if (d instanceof BlockRegionData) {
                                final BlockRegionData rd = (BlockRegionData) d;
                                final int startX = WorldUtils.toChunkCoord(rd.getPos().getX());
                                final int startZ = WorldUtils.toChunkCoord(rd.getPos().getZ());
                                final int endX = WorldUtils.toChunkCoord(rd.getPos().getX() + rd.getSize().getX());
                                final int endZ = WorldUtils.toChunkCoord(rd.getPos().getZ() + rd.getSize().getZ());

                                final int xLen = endX - startX + 1;
                                final int zLen = endZ - startZ + 1;
                                final long[] chunks2 = new long[xLen * zLen];

                                int i = 0;
                                for (int x = startX; x <= endX; x++) {
                                    for (int z = startZ; z <= endZ; z++) {
                                        chunks2[i++] = WorldUtils.packChunkKey(x, z);
                                    }
                                }
                                return chunks2;
                            } else {
                                final int x = WorldUtils.toChunkCoord(d.getPos().getX());
                                final int z = WorldUtils.toChunkCoord(d.getPos().getZ());
                                return new long[]{WorldUtils.packChunkKey(x, z)};
                            }
                        }))
                .stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (final ExecutionException e) {
                        throw new RuntimeException(e.getCause());
                    }
                })
                .collect(LongOpenHashSet::new,
                        (set, elems) -> set.addAll(new LongOpenHashSet(elems)),
                        LongOpenHashSet::addAll)
                .toArray(new long[0]);

        System.out.println("Initializing " + chunks.length + " chunks");

        pool.invokeAll(LambdaUtils2.toCallable(chunks, chunkKey -> {
            final int chunkX = WorldUtils.unpackChunkX(chunkKey);
            final int chunkY = WorldUtils.unpackChunkZ(chunkKey);
            final Vec2i c = new Vec2i(chunkX, chunkY);

            this.world.addPluginChunkTicket(chunkX, chunkY, LibraryPlugin.getPlugin());

            final Object chunk = compat.getNMSChunk(w, c);

            final WrapperPlayServerMultiBlockChange[] pkts = this.posData.stream()
                    // Filter out the blocks that are not in the chunk
                    .filter(r -> {
                        if (r instanceof BlockRegionData) {
                            final BlockRegionData rd = (BlockRegionData) r;
                            return MathUtils.contains(chunkX,
                                    WorldUtils.toChunkCoord(rd.getPos().getX()),
                                    WorldUtils.toChunkCoord(rd.getPos().getX() + rd.getSize().getX()))
                                    && MathUtils.contains(chunkY,
                                    WorldUtils.toChunkCoord(rd.getPos().getZ()),
                                    WorldUtils.toChunkCoord(rd.getPos().getZ() + rd.getSize().getZ()));
                        } else {
                            return WorldUtils.toChunkCoord(r.getPos().getX()) == chunkX
                                    && WorldUtils.toChunkCoord(r.getPos().getZ()) == chunkY;
                        }
                    })
                    .map(r -> {
                        if (r instanceof BlockRegionData) {
                            final Vec3i start = r.getPos();
                            final Vec3i size = ((BlockRegionData) r).getSize();
                            final int xS = Math.max(start.getX(), WorldUtils.toWorldCoord(chunkX, 0));
                            final int xDiff = Math.min(size.getX(), 16);
                            final int zS = Math.max(start.getZ(), WorldUtils.toWorldCoord(chunkY, 0));
                            final int zDiff = Math.min(size.getZ(), 16);
                            final int yS = Math.max(start.getY(), minY);
                            final int yDiff = Math.min(size.getY(), maxY - yS);

                            final BlockPosData[] arr = new BlockPosData[xDiff * yDiff * zDiff];

                            for (int x = 0; x < xDiff; x++) {
                                for (int z = 0; z < zDiff; z++) {
                                    for (int y = 0; y < yDiff; y++) {
                                        arr[(x * zDiff + z) * yDiff + y]
                                                = new BlockPosData(new Vec3i(x + xS, y + yS, z + zS), r.getBlockData());
                                    }
                                }
                            }
                            return arr;
                        } else {
                            return new BlockPosData[]{r};
                        }
                    })
                    .flatMap(Arrays::stream)
                    // Group by chunk section of the block
                    .collect(Collectors.groupingBy(e2 -> WorldUtils.toSectionIndex(e2.getPos().getY(), minY)))
                    // Edit the blocks in the chunk section
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final Object cs = compat.getNMSChunkSection2(chunk, e.getKey());

                        // Edit the blocks and update lights
                        final WrapperPlayServerMultiBlockChange.EncodedBlock[] placeRes = e.getValue().stream()
                                .filter((e3) -> {
                                    final Vec3i v3 = e3.getPos().clone().plus(0, -minY, 0);
                                    if (Objects.equals(compat.getWrappedBlockData(cs, v3), e3.getBlockData())) {
                                        return false;
                                    }
                                    compat.setWrappedBlockData(cs, v3, e3.getBlockData(), false);
                                    compat.updateLightsAtBlock(w, v3);
                                    return true;
                                })
                                .map(d -> new WrapperPlayServerMultiBlockChange.EncodedBlock(d.getBlockData().toPacketBlockState(), d.getPos().getX(), d.getPos().getY(), d.getPos().getZ()))
                                .toArray(WrapperPlayServerMultiBlockChange.EncodedBlock[]::new);

                        if (placeRes.length != 0) {
                            // Update the blocks in the chunk sections
                            return new WrapperPlayServerMultiBlockChange(
                                    new Vector3i(chunkX, WorldUtils.toWorldSectionY(e.getKey(), minY), chunkY), true, placeRes);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(WrapperPlayServerMultiBlockChange[]::new);

            // Send the packet to all players in the world
            this.world.getPlayers()
                    .stream()
                    .filter(p -> compat.isChunkViewing(p, w, c))
                    .map(p -> PacketEvents.getAPI().getPlayerManager().getUser(p.getUniqueId()))
                    .forEach(p -> {
                        for (final WrapperPlayServerMultiBlockChange pkt : pkts) {
                            p.sendPacket(pkt);
                        }
                    });

            this.world.removePluginChunkTicket(chunkX, chunkY, LibraryPlugin.getPlugin());
            //compat.unloadChunk(chunk, true);
        }));
        System.out.println("Initialized " + chunks.length + " chunks");

        pool.shutdown();
        try {
            if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Updated " + chunks.length + " chunks");
    }
}
