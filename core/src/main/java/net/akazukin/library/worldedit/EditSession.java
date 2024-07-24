package net.akazukin.library.worldedit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.packets.SMultiBlockChangePacket;
import net.akazukin.library.utils.ThreadUtils;
import net.akazukin.library.world.WrappedBlockData;
import org.bukkit.World;

@RequiredArgsConstructor
public class EditSession {
    private final Map<Vec3i, WrappedBlockData> vecs = new ConcurrentHashMap<>();
    private final Map<Region<?>, WrappedBlockData> regions = new ConcurrentHashMap<>();
    private final int threads;
    private final World world;

    public void setBlock(final Vec3<Integer> loc, final WrappedBlockData state) {
        this.vecs.put(loc instanceof Vec3i ? (Vec3i) loc : new Vec3i(loc), state);
    }

    public void setBlock(final Region<Vec3<?>> reg, final WrappedBlockData state) {
        final Vec3<?> f = reg.getFirst();
        final Vec3<?> s = reg.getSecond();
        final Vec3i minV = new Vec3i(
                Math.min(f.getX().intValue(), s.getX().intValue()),
                Math.min(f.getY().intValue(), s.getY().intValue()),
                Math.min(f.getZ().intValue(), s.getZ().intValue())
        );
        final Vec3i maxV = new Vec3i(
                Math.max(f.getX().intValue(), s.getX().intValue()),
                Math.max(f.getY().intValue(), s.getY().intValue()),
                Math.max(f.getZ().intValue(), s.getZ().intValue())
        );

        this.vecs.keySet().stream()
                .filter(v ->
                        minV.getX() <= v.getX() && v.getX() <= maxV.getX() &&
                                minV.getY() <= v.getY() && v.getY() <= maxV.getY() &&
                                minV.getZ() <= v.getZ() && v.getZ() <= maxV.getZ()
                )
                .forEach(this.vecs::remove);

        this.regions.put(reg, state);
    }

    public void queue() {
        this.queue(null);
    }

    public void queue(final Runnable finishSession) {
        new Thread(() -> {
            this.complete();
            if (finishSession != null) finishSession.run();
        }, "EditSession-AkzLibs").start();
    }

    public void complete() {
        final Object w = LibraryPlugin.COMPAT.getNMSWorld(this.world);
        final int min = LibraryPlugin.COMPAT.getMinHeight(this.world);

        final ExecutorService pool = Executors.newFixedThreadPool(this.threads);

        final Set<Vec2i> chunks = new HashSet<>();

        this.regions.keySet().forEach(r -> {
            final int minX = Math.min(r.getFirst().getX().intValue(), r.getSecond().getX().intValue()) >> 4;
            final int maxX = Math.max(r.getFirst().getX().intValue(), r.getSecond().getX().intValue()) >> 4;

            final int minZ = Math.min(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue()) >> 4;
            final int maxZ = Math.max(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue()) >> 4;


            IntStream.range(minX, maxX + 1).forEach(x -> IntStream.range(minZ, maxZ + 1).forEach(z ->
                    chunks.add(new Vec2i(x, z))));
        });

        this.vecs.keySet().forEach(v -> chunks.add(new Vec2i(v.getX() >> 4, v.getZ() >> 4)));

        final long s = System.nanoTime();
        chunks.forEach((c) -> this.world.addPluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin()));
        System.out.println("LoadChunks: " + (System.nanoTime() - s));


        chunks.forEach(c -> pool.submit(() -> {
            final long s6 = System.nanoTime();
            final Object chunk = LibraryPlugin.COMPAT.getNMSChunk(w, c);
            final long s7 = System.nanoTime();

            final Map<Vec3i, WrappedBlockData> pos = new HashMap<>();

            final long s2 = System.nanoTime();
            this.regions.forEach((r, b) -> {
                final int minX = Math.min(r.getFirst().getX().intValue(), r.getSecond().getX().intValue());
                final int maxX = Math.max(r.getFirst().getX().intValue(), r.getSecond().getX().intValue());

                final int minY = Math.min(r.getFirst().getY().intValue(), r.getSecond().getY().intValue());
                final int maxY = Math.max(r.getFirst().getY().intValue(), r.getSecond().getY().intValue());

                final int minZ = Math.min(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue());
                final int maxZ = Math.max(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue());

                if (minX >> 4 <= c.getX() && c.getX() <= maxX >> 4 &&
                        minZ >> 4 <= c.getY() && c.getY() <= maxZ >> 4) {
                    IntStream.range(Math.max(minX, c.getX() * 16), Math.min(maxX, (c.getX() * 16) + 15) + 1).forEach(x ->
                            IntStream.range(minY, maxY + 1).forEach(y ->
                                    IntStream.range(Math.max(minZ, c.getY() * 16),
                                            Math.min(maxZ, (c.getY() * 16) + 15) + 1).forEach(z ->
                                            pos.put(new Vec3i(x, y, z), b)
                                    )));
                }
            });

            this.vecs.entrySet().stream().filter(e ->
                    (c.getX() * 16) < e.getKey().getX() && e.getKey().getX() < (c.getX() * 16) + 15 &&
                            (c.getY() * 16) < e.getKey().getZ() && e.getKey().getZ() < (c.getY() * 16) + 15
            ).forEach(e -> pos.put(e.getKey(), e.getValue()));
            final long s10 = System.nanoTime();

            final AtomicLong l = new AtomicLong();
            final AtomicLong l2 = new AtomicLong();
            final AtomicLong l3 = new AtomicLong();
            final AtomicLong l4 = new AtomicLong();
            final AtomicLong l5 = new AtomicLong();
            final AtomicLong l6 = new AtomicLong();
            final AtomicLong l7 = new AtomicLong();

            ThreadUtils.parallel(() -> pos.entrySet().stream()
                    .collect(Collectors.groupingBy(e2 -> new Vec3i(
                            e2.getKey().getX() >> 4, e2.getKey().getY() >> 4,
                            e2.getKey().getZ() >> 4
                    )))
                    .entrySet().parallelStream()
                    .forEach(e -> {
                        final Object cs = LibraryPlugin.COMPAT.getNMSChunkSection2(chunk,
                                e.getKey().getY() - (min >> 4));

                        e.getValue().parallelStream()
                                .map((e3) -> {
                                    final Vec3i v3 = e3.getKey().clone().add(0, -min, 0);
                                    final long s3 = System.nanoTime();
                                    final boolean res = Objects.equals(
                                            LibraryPlugin.COMPAT.getBlockDate2(cs, v3),
                                            e3.getValue());
                                    final long s4 = System.nanoTime();
                                    l.set(l.get() + (s4 - s3));
                                    if (res) return null;
                                    LibraryPlugin.COMPAT.setBlockDate2(cs, v3, e3.getValue(),
                                            false);
                                    final long s5 = System.nanoTime();
                                    l2.set(l2.get() + (s5 - s4));
                                    LibraryPlugin.COMPAT.updateLightsAtBlock(w, v3);
                                    l3.set(l3.get() + (System.nanoTime() - s5));
                                    return new PlaceResult(e3.getKey(), e3.getValue(), cs);
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.groupingBy(PlaceResult::getChunkSection))
                                .entrySet().parallelStream()
                                .forEach(e2 -> {
                                    final long s3 = System.nanoTime();
                                    final SMultiBlockChangePacket pkt =
                                            new SMultiBlockChangePacket(
                                                    e.getKey(),
                                                    e2.getValue().parallelStream()
                                                            .map(result -> new SMultiBlockChangePacket.BlockInfo(
                                                                    result.getVec3(),
                                                                    result.getBlockData()))
                                                            .toArray(SMultiBlockChangePacket.BlockInfo[]::new)
                                            );
                                    final long s4 = System.nanoTime();
                                    l4.set(l4.get() + (s4 - s3));
                                    this.world.getPlayers().forEach(p ->
                                            LibraryPlugin.COMPAT.sendPacket(p, pkt));
                                    l5.set(l5.get() + (System.nanoTime() - s4));
                                });
                    }), 100);

            final long s8 = System.nanoTime();
            this.world.removePluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin());
            //LibraryPlugin.COMPAT.unloadChunk(chunk, true);
            final long s9 = System.nanoTime();

            System.out.println("getChunk: " + (s7 - s6)
                    + ", InitRegion: " + (s10 - s2)
                    + ", CheckBlock: " + l.get()
                    + ", SetBlock: " + l2.get()
                    + ", UpdateLight: " + l3.get()
                    + ", InitPkts: " + l4.get()
                    + ", SendPkts: " + l5.get()
                    + ", UnloadChunk: " + (s9 - s8)
                    + ", Total: " + (System.nanoTime() - s6)
            );
        }));


        /*this.vecs.entrySet().stream()
                .collect(Collectors.groupingBy(e ->
                        new Vec2i(e.getKey().getX() >> 4, e.getKey().getZ() >> 4)))
                .forEach((v, e) -> pool.submit(() -> ThreadUtils.parallel(() -> {
                    final Object chunk = LibraryPlugin.COMPAT.getNMSChunk(w, v);

                    e.parallelStream()
                            .collect(Collectors.groupingBy(e2 -> new Vec3i(
                                    e2.getKey().getX() >> 4, e2.getKey().getY() >> 4,
                                    e2.getKey().getZ() >> 4
                            )))
                            .forEach((v2, e2) -> {
                                final Object cs = LibraryPlugin.COMPAT.getNMSChunkSection2(chunk,
                                        v2.getY() - (min >> 4));

                                e2.parallelStream()
                                        .map((e3) -> {
                                            final Vec3i v3 = e3.getKey().clone().add(0, -min, 0);
                                            if (Objects.equals(
                                                    LibraryPlugin.COMPAT.getBlockDate2(cs, v3),
                                                    e3.getValue()))
                                                return null;
                                            LibraryPlugin.COMPAT.setBlockDate2(cs, v3, e3.getValue(),
                                                    false);
                                            LibraryPlugin.COMPAT.updateLightsAtBlock(w, v3);
                                            return new PlaceResult(e3.getKey(), e3.getValue(), cs);
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.groupingBy(PlaceResult::getChunkSection))
                                        .forEach((chunkSection, results) -> {
                                            final SMultiBlockChangePacket pkt =
                                                    new SMultiBlockChangePacket(
                                                            v2,
                                                            results.parallelStream()
                                                                    .map(result -> new SMultiBlockChangePacket
                                                                    .BlockInfo(
                                                                            result.getVec3(),
                                                                            result.getBlockData()))
                                                                    .toArray(SMultiBlockChangePacket.BlockInfo[]::new)
                                                    );
                                            this.world.getPlayers().forEach(p ->
                                                    LibraryPlugin.COMPAT.sendPacket(p, pkt));
                                        });
                            });
                    LibraryPlugin.COMPAT.unloadChunk(chunk, true);
                    //this.world.unloadChunk(v.getX(), v.getY(), true);
                }, 12)));*/

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdownNow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class PlaceResult {
        Vec3<Integer> vec3;
        WrappedBlockData blockData;
        Object chunkSection;
    }
}
