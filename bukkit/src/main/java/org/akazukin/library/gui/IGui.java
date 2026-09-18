package org.akazukin.library.gui;

import java.util.UUID;

public interface IGui {
    OpenStatus open(final UUID player);

    void close(final UUID player);

    default void onOpen(final UUID player) {
    }

    default void onClose(final UUID player) {
    }

    default void destroy() {
    }
}
