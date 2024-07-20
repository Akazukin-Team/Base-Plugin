package net.akazukin.library.worldedit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Vec2i implements Vec2<Integer> {
    Integer x;
    Integer y;

    public Vec2i(final Vec2<?> loc) {
        this(loc.getX().intValue(), loc.getY().intValue());
    }

    @Override
    public Vec2i setX(final Integer x) {
        this.x = x;
        return this;
    }

    @Override
    public Vec2i setY(final Integer y) {
        this.y = y;
        return this;
    }

    @Override
    public Vec2i add(final Integer x, final Integer y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public Vec2i clone() {
        return new Vec2i(this);
    }
}
