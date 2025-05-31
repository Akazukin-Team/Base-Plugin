package org.akazukin.library.worldedit;

public interface Vec3<N extends Number> extends Cloneable {
    N getX();

    N getY();

    N getZ();

    Vec3<N> plus(N x, N y, N z);

    Vec3<N> minus(N x, N y, N z);

    Vec3<N> minus(final Vec3<N> value);

    Vec3<N> plus(final Vec3<N> value);

    Vec3<N> negate();

    Vec3<N> clone();
}
