package net.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vec3i implements Vec3<Integer> {
    Integer x;
    Integer y;
    Integer z;

    public Vec3i(final Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Vec3i(final Vec3<?> loc) {
        this(loc.getX().intValue(), loc.getY().intValue(), loc.getZ().intValue());
    }

    @Override
    public Vec3i setX(final Integer x) {
        this.x = x;
        return this;
    }

    @Override
    public Vec3i setY(final Integer y) {
        this.y = y;
        return this;
    }

    @Override
    public Vec3i setZ(final Integer z) {
        this.z = z;
        return this;
    }

    @Override
    public Vec3i clone() {
        Vec3i ins = null;
        try {
            ins = (Vec3i) super.clone();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return ins;
    }
}
