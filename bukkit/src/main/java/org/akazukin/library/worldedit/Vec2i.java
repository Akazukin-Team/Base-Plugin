package org.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Vec2i implements Vec2<Integer> {
    @NotNull Integer x, y;

    public Vec2i(final Vec2<?> loc) {
        this(loc.getX().intValue(), loc.getY().intValue());
    }

    @Override
    public Vec2i clone() {
        return new Vec2i(this);
    }

    @Override
    public Vec2i minus(final Integer x, final Integer y) {
        return this.plus(-x, -y);
    }

    @Override
    public Vec2i plus(final Integer x, final Integer y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public Vec2<Integer> minus(final Vec2<Integer> value) {
        return value.clone().negate();
    }

    @Override
    public Vec2i plus(final Vec2<Integer> value) {
        return this.plus(value.getX(), value.getY());
    }

    @Override
    public Vec2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }
}
