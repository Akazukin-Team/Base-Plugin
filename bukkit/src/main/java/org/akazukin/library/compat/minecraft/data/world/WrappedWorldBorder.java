package org.akazukin.library.compat.minecraft.data.world;

import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

@Getter
@Setter
public class WrappedWorldBorder implements WorldBorder {
    private double size;
    private long time;
    private Location center;
    private double damageAmount;
    private double damageBuffer;
    private int warningDistance;
    private int warningTime;

    @Override
    public void reset() {
        this.setSize(6.0E7);
        this.setDamageAmount(0.2);
        this.setDamageBuffer(5.0);
        this.setWarningDistance(5);
        this.setWarningTime(15);
        this.setCenter(0.0, 0.0);
    }

    @Override
    public void setSize(final double size, final long time) {
        this.size = size;
        this.time = time;
    }

    @Override
    public void setCenter(final double x, final double z) {
        if (center == null) {
            center = new Location(null, x, 0, z);
        } else {
            center.setX(x);
            center.setZ(x);
        }
    }

    @Override
    @SuppressWarnings("null")
    public boolean isInside(@Nonnull final Location location) {
        return location.getWorld().equals(this.center.getWorld()) &&
                (center.getX() - size) <= location.getX() && location.getX() + size >= location.getX() && (location.getZ() - size) <= location.getZ() && location.getZ() + size >= location.getZ();
    }
}
