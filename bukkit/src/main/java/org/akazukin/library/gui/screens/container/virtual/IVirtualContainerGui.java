package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import org.akazukin.library.container.VirtualContainer;
import org.akazukin.library.gui.IGui;

public interface IVirtualContainerGui extends IGui {
    default boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack clickedItem) {
        return false;
    }

    VirtualContainer getInv();
}
