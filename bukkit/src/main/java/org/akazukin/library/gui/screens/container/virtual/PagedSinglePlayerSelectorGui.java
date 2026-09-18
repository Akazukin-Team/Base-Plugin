package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import net.kyori.adventure.text.Component;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.utils.PacketItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public abstract class PagedSinglePlayerSelectorGui extends PagedSingleSelectorGui<UUID> {
    protected PagedSinglePlayerSelectorGui(final String title, final int rows, final II18nLang locale, final IGui prevGui,
                                           final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);
    }

    @Override
    protected ItemStack toItemStack(final UUID element) {
        final ItemStack item = PacketItemUtils.getSkullItem(element);
        final OfflinePlayer offP = Bukkit.getOfflinePlayer(element);
        final String displayName = offP.getName() != null ? offP.getName() : element.toString();
        PacketItemUtils.setDisplayName(item, Component.text("§a" + displayName));
        return item;
    }
}
