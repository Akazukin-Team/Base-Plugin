package net.akazukin.library.worldedit;

public interface Vec3<N extends Number> {
    N getX();

    Vec3<N> setX(N x);

    N getY();

    Vec3<N> setY(N x);

    N getZ();

    Vec3<N> setZ(N x);

    Vec3<N> add(N x, N y, N z);

    Vec3<N> clone();
}
