package net.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Vec3d implements Vec3<Double> {
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
    public void add(final Double x, final Double y, final Double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public Vec3d clone() {
        return new Vec3d(this);
    }
}
