package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vec3d implements Vec3<Double> {
    @NotNull Double x, y, z;

    public Vec3d(final Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Vec3d(final Vec3<?> loc) {
        this(loc.getX().doubleValue(), loc.getY().doubleValue(), loc.getZ().doubleValue());
    }

    @Override
    public Vec3d clone() {
        return new Vec3d(this);
    }

    @Override
    public Vec3<Double> minus(final Vec3<Double> value) {
        return this.plus(value.clone().negate());
    }

    @Override
    public Vec3d plus(final Vec3<Double> value) {
        return this.plus(value.getX(), value.getY(), value.getZ());
    }

    @Override
    public Vec3<Double> negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vec3d plus(final Double x, final Double y, final Double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Vec3d minus(final Double x, final Double y, final Double z) {
        return this.plus(-x, -y, -z);
    }
}
