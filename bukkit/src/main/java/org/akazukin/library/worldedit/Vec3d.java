package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vec3d {
    double x, y, z;

    public Vec3d(final Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Vec3d(final Vec3d loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Vec3d minus(final Vec3d value) {
        return this.plus(value.clone().negate());
    }

    public Vec3d plus(final Vec3d value) {
        return this.plus(value.getX(), value.getY(), value.getZ());
    }

    public Vec3d plus(final double x, final double y, final double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3d negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vec3d clone() {
        return new Vec3d(this);
    }

    public Vec3d minus(final double x, final double y, final double z) {
        return this.plus(-x, -y, -z);
    }
}
