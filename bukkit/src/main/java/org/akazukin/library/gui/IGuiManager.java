package org.akazukin.library.gui;

import java.util.UUID;

public interface IGuiManager {
    void openGui(UUID player, IGui gui);

    void closeGui(UUID player);

    void closeGui(UUID player, IGui gui);

    void handleCloseGui(UUID player, IGui gui);

    void handleOpenGui(UUID player, IGui gui);
}
