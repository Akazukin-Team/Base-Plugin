package org.akazukin.library.worldedit;

public interface Vec2<N extends Number> extends Cloneable {
    N getX();

    N getY();

    Vec2<N> plus(final N x, final N y);

    Vec2<N> minus(final N x, final N y);

    Vec2<N> plus(final Vec2<N> value);

    Vec2<N> minus(final Vec2<N> value);

    Vec2<N> negate();

    Vec2<N> clone();
}
