package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.packets.SMultiBlockChangePacket;
import org.akazukin.library.utils.ThreadUtils;
import org.akazukin.library.world.WrappedBlockData;
import org.akazukin.util.utils.MathUtils;
import org.bukkit.World;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            if (finishSession != null) {
                finishSession.run();
            }
        }, "EditSession-AkzLibs").start();
    }

    public void complete() {
        final Object w = LibraryPlugin.getPlugin().getCompat().getNMSWorld(this.world);
        final int min = LibraryPlugin.getPlugin().getCompat().getMinHeight(this.world);

        final ExecutorService pool = Executors.newFixedThreadPool(this.threads);

        final List<Vec2i> chunks = Collections.synchronizedList(new ArrayList<>());

        this.regions.keySet().parallelStream().forEach(r -> {
            final int minX = Math.min(r.getFirst().getX().intValue(), r.getSecond().getX().intValue()) >> 4;
            final int maxX = Math.max(r.getFirst().getX().intValue(), r.getSecond().getX().intValue()) >> 4;

            final int minZ = Math.min(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue()) >> 4;
            final int maxZ = Math.max(r.getFirst().getZ().intValue(), r.getSecond().getZ().intValue()) >> 4;

            IntStream.range(minX, maxX + 1).parallel().forEach(x -> IntStream.range(minZ, maxZ + 1).forEach(z ->
                    chunks.add(new Vec2i(x, z))));
        });

        this.vecs.keySet().parallelStream().forEach(v -> chunks.add(new Vec2i(v.getX() >> 4, v.getZ() >> 4)));

        chunks.forEach((c) -> this.world.addPluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin()));


        chunks.forEach(c -> pool.submit(() -> ThreadUtils.parallel(() -> {
            final Object chunk = LibraryPlugin.getPlugin().getCompat().getNMSChunk(w, c);

            final Map<Vec3<Integer>, WrappedBlockData> pos = new HashMap<>(
                    this.regions.entrySet().parallelStream().map(r -> {
                                final Vec3<?> vec = r.getKey().getFirst();
                                final Vec3<?> vec2 = r.getKey().getSecond();
                                final int minX = Math.min(vec.getX().intValue(), vec2.getX().intValue());
                                final int maxX = Math.max(vec.getX().intValue(), vec2.getX().intValue());

                                final int minY = Math.min(vec.getY().intValue(), vec2.getY().intValue());
                                final int maxY = Math.max(vec.getY().intValue(), vec2.getY().intValue());

                                final int minZ = Math.min(vec.getZ().intValue(), vec2.getZ().intValue());
                                final int maxZ = Math.max(vec.getZ().intValue(), vec2.getZ().intValue());

                                if (MathUtils.contains(c.getX(), minX >> 4, maxX >> 4) &&
                                        MathUtils.contains(c.getY(), minZ >> 4, maxZ >> 4)) {
                                    final List<Map.Entry<Vec3<Integer>, WrappedBlockData>> sets2 = new ArrayList<>();

                                    IntStream.range(Math.max(minX, c.getX() << 4),
                                            Math.min(maxX, (c.getX() << 4) + 15) + 1).forEach(x ->
                                            IntStream.range(Math.max(minZ, c.getY() << 4),
                                                    Math.min(maxZ, (c.getY() << 4) + 15) + 1).forEach(z ->
                                                    IntStream.range(minY, maxY + 1).forEach(y ->
                                                            sets2.add(new AbstractMap.SimpleEntry<>(new Vec3i(x, y, z),
                                                                    r.getValue()))
                                                    )));
                                    return sets2;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            pos.putAll(this.vecs.entrySet().parallelStream().filter(e ->
                            c.getX() == e.getKey().getX() >> 4 && c.getY() == e.getKey().getZ() >> 4)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            pos.entrySet().stream()
                    .collect(Collectors.groupingBy(e2 -> new Vec3i(
                            e2.getKey().getX() >> 4, e2.getKey().getY() >> 4,
                            e2.getKey().getZ() >> 4
                    )))
                    .entrySet().parallelStream()
                    .forEach(e -> {
                        final Object cs = LibraryPlugin.getPlugin().getCompat().getNMSChunkSection2(chunk,
                                e.getKey().getY() - (min >> 4));

                        e.getValue().parallelStream()
                                .map((e3) -> {
                                    final Vec3<Integer> v3 = e3.getKey().clone();
                                    v3.add(0, -min, 0);
                                    final long s3 = System.nanoTime();
                                    final boolean res = Objects.equals(LibraryPlugin.getPlugin().getCompat().getWrappedBlockData(cs, v3), e3.getValue());
                                    if (res) {
                                        return null;
                                    }
                                    LibraryPlugin.getPlugin().getCompat().setWrappedBlockData(cs, v3, e3.getValue(), false);
                                    LibraryPlugin.getPlugin().getCompat().updateLightsAtBlock(w, v3);
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
                                    this.world.getPlayers().forEach(p ->
                                            LibraryPlugin.getPlugin().getCompat().sendPacket(p, pkt));
                                });
                    });

            this.world.removePluginChunkTicket(c.getX(), c.getY(), LibraryPlugin.getPlugin());
            //LibraryPlugin.getPlugin().getCompat().unloadChunk(chunk, true);
        }, 100)));

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
