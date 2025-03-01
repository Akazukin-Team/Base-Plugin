package org.akazukin.library.gui.screens.chest.paged;

import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class GuiPagedSinglePlayerSelector extends GuiPagedSingleSelector {
    protected OfflinePlayer selectedPlayer;

    public GuiPagedSinglePlayerSelector(final String title, final int maxRows, final int minRows, final Player player,
                                        final OfflinePlayer[] players, final GuiBase prevGui) {
        super(title, maxRows, minRows, player, Arrays.stream(players).map(p -> {
            final ItemStack item = ItemUtils.getSkullItem(p);
            ItemUtils.setDisplayName(item, "§a" + p.getName());
            return item;
        }).toArray(ItemStack[]::new), prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (super.onGuiClick(event)) {
            if (this.selected == null || !LibraryPlugin.getPlugin().getCompat().containsPlData(this.selected, "HEAD_UUID")) {
                this.selected = null;
                this.selectedPlayer = null;
                return true;
            }

            final UUID uuid = UUID.fromString(LibraryPlugin.getPlugin().getCompat().getPlDataString(this.selected, "HEAD_UUID"));
            this.selectedPlayer = Bukkit.getOfflinePlayer(uuid);
            return true;
        }
        return false;
    }
}
