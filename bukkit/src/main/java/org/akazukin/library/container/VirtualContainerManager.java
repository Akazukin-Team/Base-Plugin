package org.akazukin.library.container;

import org.jetbrains.annotations.Nullable;

public class VirtualContainerManager {
    int id;

    public VirtualContainer createContainer(@Nullable final String title, final int rows) {
        return new VirtualContainer(this.id++ % 128, title, rows);
    }
}
