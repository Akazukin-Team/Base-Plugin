package org.akazukin.library.worldedit;

public interface Vec2<N extends Number> extends Cloneable {
    N getX();

    void setX(N x);

    N getY();

    void setY(N y);

    void add(N x, N y);

    Vec2<N> clone();
}
