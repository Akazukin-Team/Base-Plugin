package org.akazukin.library.packetlistener;

/**
 * A pseudo implementation of pointers like in C.
 *
 * <p>
 * A pointer is used to warp an object. You can use it to pass wrapped object
 * into methods and handle their container between each method.
 * </p>
 *
 * <p>
 * However, when you modify {@code content}, it will not apply the change in
 * memory change like in C. To know pointer's content at any time, you can look
 * at {@code content} data.
 * </p>
 *
 * @param <T> Type of {@code content}.
 * @author DrogoniEntity
 */
public class Pointer<T> {
    /**
     * Pointer's content.
     *
     * <p>
     * You're free to change this value to point to another object.
     * </p>
     */
    public T content;

    /**
     * Initialize pointer with a pre-defined content.
     *
     * @param content - pre-defined value.
     */
    public Pointer(final T content) {
        this.content = content;
    }
}
