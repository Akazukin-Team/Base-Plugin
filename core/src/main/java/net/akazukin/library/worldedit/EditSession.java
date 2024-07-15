package net.akazukin.library.worldedit;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<Vec3i, WrappedBlockData> blocks = new ConcurrentHashMap<>();
    private final int threads;
    private final World world;

    public void setBlock(final Vec3<Integer> loc, final WrappedBlockData state) {
        this.blocks.put(loc instanceof Vec3i ? (Vec3i) loc : new Vec3i(loc), state);
    }

    public void setBlock(final Region<Vec3<Integer>> reg, final WrappedBlockData state) {
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

        ThreadUtils.parallel(() -> IntStream.range(minV.getX(), maxV.getX() + 1).parallel().forEach(x ->
                IntStream.range(minV.getY(), maxV.getY() + 1).parallel().forEach(y ->
                        IntStream.range(minV.getZ(), maxV.getZ() + 1).parallel().forEach(z ->
                                this.blocks.put(new Vec3i(x, y, z), state)
                        ))), this.threads);
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

        ThreadUtils.parallel(() ->
                this.blocks.entrySet().parallelStream()
                        .collect(Collectors.groupingBy(e ->
                                new Vec2i(e.getKey().getX() >> 4, e.getKey().getZ() >> 4)))
                        .forEach((v, e) -> {
                            final Object chunk = LibraryPlugin.COMPAT.getNMSChunk(w, v);

                            e.parallelStream()
                                    .collect(Collectors.groupingBy(e2 -> new Vec3i(
                                            e2.getKey().getX() >> 4, e2.getKey().getY() >> 4, e2.getKey().getZ() >> 4
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
                                                    LibraryPlugin.COMPAT.setBlockDate2(cs, v3, e3.getValue(), false);
                                                    LibraryPlugin.COMPAT.updateLightsAtBlock(w, v3);
                                                    return new PlaceResult(e3.getKey(), e3.getValue(), cs);
                                                })
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.groupingBy(PlaceResult::getChunkSection))
                                                .forEach((chunkSection, results) -> {
                                                    final SMultiBlockChangePacket pkt = new SMultiBlockChangePacket(
                                                            v2,
                                                            results.parallelStream()
                                                                    .map(result -> new SMultiBlockChangePacket.BlockInfo(
                                                                            result.getVec3(), result.getBlockData()))
                                                                    .toArray(SMultiBlockChangePacket.BlockInfo[]::new)
                                                    );
                                                    this.world.getPlayers().forEach(p ->
                                                            LibraryPlugin.COMPAT.sendPacket(p, pkt));
                                                });
                                    });
                            //LibraryPlugin.COMPAT.updateLightsAtChunk(chunk);
                        }), this.threads);
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
