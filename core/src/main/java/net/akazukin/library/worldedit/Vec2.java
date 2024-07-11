package net.akazukin.library.worldedit;

public interface Vec2<N extends Number> {
    N getX();

    Vec2<N> setX(N x);

    N getY();

    Vec2<N> setY(N y);

    Vec2<N> clone();
}
