package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vec3i {
    int x, y, z;

    public Vec3i(final Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Vec3i(final Vec3i loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Vec3i minus(final Vec3i value) {
        return this.plus(value.clone().negate());
    }

    public Vec3i plus(final Vec3i value) {
        return this.plus(value.getX(), value.getY(), value.getZ());
    }

    public Vec3i plus(final int x, final int y, final int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3i negate() {
        return new Vec3i(-this.x, -this.y, -this.z);
    }

    @Override
    public Vec3i clone() {
        return new Vec3i(this);
    }

    public Vec3i minus(final int x, final int y, final int z) {
        return this.plus(-x, -y, -z);
    }
}
