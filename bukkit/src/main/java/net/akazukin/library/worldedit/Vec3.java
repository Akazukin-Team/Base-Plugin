package net.akazukin.library.worldedit;

public interface Vec3<N extends Number> extends Cloneable {
    N getX();

    void setX(N x);

    N getY();

    void setY(N y);

    N getZ();

    void setZ(N z);

    void add(N x, N y, N z);

    Vec3<N> clone();
}
