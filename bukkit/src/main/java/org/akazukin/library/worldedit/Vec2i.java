package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Vec2i {
    int x, y;

    public Vec2i(final Vec2i loc) {
        this(loc.getX(), loc.getY());
    }

    public Vec2i minus(final int x, final int y) {
        return this.plus(-x, -y);
    }

    public Vec2i plus(final int x, final int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2i minus(final Vec2i value) {
        return value.clone().negate();
    }

    public Vec2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vec2i clone() {
        return new Vec2i(this);
    }

    public Vec2i plus(final Vec2i value) {
        return this.plus(value.getX(), value.getY());
    }
}
