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
public class Vec3d implements Vec3<Double>, Cloneable {
    private final static Vec3d INSTANCE = new Vec3d();

    Double x;
    Double y;
    Double z;

    public Vec3d(final Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Vec3d(final Vec3<?> loc) {
        this(loc.getX().doubleValue(), loc.getY().doubleValue(), loc.getZ().doubleValue());
    }

    @Override
    public Vec3d setX(final Double x) {
        this.x = x;
        return this;
    }

    @Override
    public Vec3d setY(final Double y) {
        this.y = y;
        return this;
    }

    @Override
    public Vec3d setZ(final Double z) {
        this.z = z;
        return this;
    }

    @Override
    public Vec3d add(final Double x, final Double y, final Double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Vec3d clone() {
        return new Vec3d(this);
    }
}
