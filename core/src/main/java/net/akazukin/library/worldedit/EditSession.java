package net.akazukin.library.worldedit;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.world.WrappedBlockData;
import org.bukkit.World;

@RequiredArgsConstructor
public class EditSession {
    private final Map<Vec3i, WrappedBlockData> blocks = new HashMap<>();
    private final World world;

    public void setBlock(final Vec3<?> loc, final WrappedBlockData state) {
        this.blocks.put(loc instanceof Vec3i ? (Vec3i) loc : new Vec3i(loc), state);
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


        for (int x = minV.getX(); x <= maxV.getX(); x++) {
            for (int y = minV.getY(); y <= maxV.getY(); y++) {
                for (int z = minV.getZ(); z <= maxV.getZ(); z++) {
                    this.blocks.put(new Vec3i(x, y, z), state);
                }
            }
        }
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

        this.blocks
                .entrySet()
                .parallelStream()
                .collect(Collectors.groupingBy(e -> new Vec2i(e.getKey().getX() >> 4, e.getKey().getZ() >> 4)))
                .forEach((v, e) -> {
                    final Object chunk = LibraryPlugin.COMPAT.getNMSChunk(w, v);
                    e.parallelStream().forEach((e2) ->
                            LibraryPlugin.COMPAT.setBlockDate(chunk, e2.getKey(), e2.getValue(), false));
                    LibraryPlugin.COMPAT.updateChunk(w, v);
                });
    }
}
