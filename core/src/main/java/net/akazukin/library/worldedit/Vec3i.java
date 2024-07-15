package net.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
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
    public Vec3i add(final Integer x, final Integer y, final Integer z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Vec3i clone() {
        return new Vec3i(this);
    }
}
