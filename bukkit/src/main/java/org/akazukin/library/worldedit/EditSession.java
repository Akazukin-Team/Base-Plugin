package org.akazukin.library.worldedit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.packets.SMultiBlockChangePacket;
import org.akazukin.library.world.WrappedBlockData;
import org.akazukin.util.utils.LambdaUtils;
import org.akazukin.util.utils.MathUtils;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    public void setBlock(final Vec3<Integer> loc, final WrappedBlockData state) {
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
    public void setBlock(final Vec3<Integer> pos, final Vec3<Integer> pos2, final WrappedBlockData state) {
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

        final Object w = LibraryPlugin.getPlugin().getCompat().getNMSWorld(this.world);
        final int maxY = this.world.getMaxHeight();
        final int minY = LibraryPlugin.getPlugin().getCompat().getMinHeight(this.world);

        final ExecutorService pool = Executors.newFixedThreadPool(this.threads);

        final Set<Vec2<Integer>> chunks = pool.invokeAll(
                        LambdaUtils.toCallable(this.posData, d -> {
                            if (d instanceof BlockRegionData) {
                                final Vec2i start = new Vec2i(d.getPos().getX() >> 4,
                                        d.getPos().getZ() >> 4);
                                final Vec2i end = new Vec2i((d.getPos().getX() + ((BlockRegionData) d).getSize().getX()) >> 4,
                                        (d.getPos().getZ() + ((BlockRegionData) d).getSize().getZ()) >> 4);

                                final List<Vec2i> chunks2 = new ArrayList<>();
                                for (int x = start.getX(); x <= end.getX(); x++) {
                                    for (int y = start.getY(); y <= end.getY(); y++) {
                                        chunks2.add(new Vec2i(x, y));
                                    }
                                }
                                return chunks2.toArray(new Vec2i[0]);
                            } else {
                                return new Vec2i[]{new Vec2i(d.getPos().getX() >> 4, d.getPos().getZ() >> 4)};
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
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
/*
        final Set<Vec2<Integer>> chunks = new HashSet<>();
        pool.invokeAll(LambdaUtils.toCallable(this.posData, d -> {
            final Vec2i start = new Vec2i(d.getPos().getX() >> 4, d.getPos().getZ() >> 4);
            if (d instanceof BlockRegionData) {
                final List<Vec2<Integer>> chunks2 = new ArrayList<>();
                for (int x = start.getX(),
                     xE = (d.getPos().getX() + ((BlockRegionData) d).getSize().getX()) >> 4;
                     x <= xE; x++) {
                    for (int y = start.getY(),
                         yE = (d.getPos().getZ() + ((BlockRegionData) d).getSize().getZ()) >> 4;
                         y <= yE; y++) {
                        chunks2.add(new Vec2i(x, y));
                    }
                }
                synchronized (chunks) {
                    chunks.addAll(chunks2);
                }
            } else {
                synchronized (chunks) {
                    chunks.add(start);
                }
            }
        }));
*/
        System.out.println("Initializing " + chunks.size() + " chunks");

        //chunks.forEach(c -> this.world.addPluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin()));


        pool.invokeAll(LambdaUtils.toCallable(chunks, c -> {
            this.world.addPluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin());

            final Object chunk = LibraryPlugin.getPlugin().getCompat().getNMSChunk(w, c);

            final SMultiBlockChangePacket[] pkts = this.posData.stream()
                    // Filter out the blocks that are not in the chunk
                    .filter(r -> {
                        if (r instanceof BlockRegionData) {
                            return MathUtils.contains(c.getX(),
                                    r.getPos().getX() >> 4,
                                    (r.getPos().getX() + ((BlockRegionData) r).getSize().getX()) >> 4)
                                    && MathUtils.contains(c.getY(),
                                    r.getPos().getZ() >> 4,
                                    (r.getPos().getZ() + ((BlockRegionData) r).getSize().getZ()) >> 4);
                        } else {
                            return (r.getPos().getX() >> 4) == c.getX() && (r.getPos().getZ() >> 4) == c.getY();
                        }
                    })
                    .map(r -> {
                        if (r instanceof BlockRegionData) {
                            final Vec3<Integer> start = r.getPos();
                            final Vec3<Integer> size = ((BlockRegionData) r).getSize();
                            final int xS = Math.max(start.getX(), c.getX() << 4);
                            //final int xE = Math.min(start.getX() + size.getX() + 1, (c.getX() + 1) << 4);
                            final int xDiff = Math.min(size.getX(), 16);
                            final int zS = Math.max(start.getZ(), c.getY() << 4);
                            //final int zE = Math.min(start.getZ() + size.getZ() + 1, (c.getY() + 1) << 4);
                            final int zDiff = Math.min(size.getZ(), 16);
                            final int yS = Math.max(start.getY(), minY);
                            //final int yE = Math.min(start.getY() + size.getY(), maxY);
                            final int yDiff = Math.min(size.getY(), maxY - yS);

                            final BlockPosData[] arr = new BlockPosData[xDiff * yDiff * zDiff];

                            for (int x = 0, i = 0;
                                 x < xDiff;
                                 x++) {
                                for (int z = 0;
                                     z < zDiff;
                                     z++) {
                                    for (int y = 0;
                                         y < yDiff;
                                         y++, i++) {
                                        arr[i] = new BlockPosData(new Vec3i(x + xS, y + yS, z + zS), r.getBlockData());
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
                    .collect(Collectors.groupingBy(e2 -> (e2.getPos().getY() - minY) >> 4))
                    // Edit the blocks in the chunk section
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final Object cs = LibraryPlugin.getPlugin().getCompat()
                                .getNMSChunkSection2(chunk, e.getKey());

                        // Edit the blocks and update lights
                        final SMultiBlockChangePacket.BlockInfo[] placeRes = e.getValue().stream()
                                .filter((e3) -> {
                                    final Vec3<Integer> v3 = e3.getPos().clone().plus(0, -minY, 0);
                                    if (Objects.equals(LibraryPlugin.getPlugin().getCompat()
                                            .getWrappedBlockData(cs, v3), e3.getBlockData())) {
                                        return false;
                                    }
                                    LibraryPlugin.getPlugin().getCompat().setWrappedBlockData(cs, v3, e3.getBlockData(), false);
                                    LibraryPlugin.getPlugin().getCompat().updateLightsAtBlock(w, v3);
                                    return true;
                                })
                                .map(d -> new SMultiBlockChangePacket.BlockInfo(d.getPos(), d.getBlockData()))
                                .toArray(SMultiBlockChangePacket.BlockInfo[]::new);

                        if (placeRes.length != 0) {
                            // Update the blocks in the chunk sections
                            return new SMultiBlockChangePacket(
                                    new Vec3i(c.getX(), e.getKey() + (minY >> 4), c.getY()), placeRes);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(SMultiBlockChangePacket[]::new);

            // Send the packet to all players in the world
            this.world.getPlayers()
                    .stream()
                    .filter(p -> LibraryPlugin.getPlugin().getCompat().isChunkViewing(p, w, c))
                    .forEach(p -> {
                        for (final SMultiBlockChangePacket pkt : pkts) {
                            LibraryPlugin.getPlugin().getCompat().sendPacket(p, pkt);
                        }
                    });

            this.world.removePluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin());
            //LibraryPlugin.getPlugin().getCompat().unloadChunk(chunk, true);
        }));
        System.out.println("Initialized " + chunks.size() + " chunks");

        pool.shutdown();
        try {
            if (!pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Updated " + chunks.size() + " chunks");
    }
}
