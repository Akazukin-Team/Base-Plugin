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
public class Vec3i implements Vec3<Integer> {
    @NotNull Integer x, y, z;

    public Vec3i(final Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Vec3i(final Vec3<?> loc) {
        this(loc.getX().intValue(), loc.getY().intValue(), loc.getZ().intValue());
    }

    @Override
    public Vec3i clone() {
        return new Vec3i(this);
    }

    @Override
    public Vec3i minus(final Vec3<Integer> value) {
        return this.plus(value.clone().negate());
    }

    @Override
    public Vec3i plus(final Vec3<Integer> value) {
        return this.plus(value.getX(), value.getY(), value.getZ());
    }

    @Override
    public Vec3i plus(final Integer x, final Integer y, final Integer z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Vec3i minus(final Integer x, final Integer y, final Integer z) {
        return this.plus(-x, -y, -z);
    }

    @Override
    public Vec3i negate() {
        return new Vec3i(-this.x, -this.y, -this.z);
    }


}
