package net.akazukin.library.gui.screens.chest.paged;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class GuiPagedSinglePlayerSelector extends GuiPagedSingleSelector {
    protected OfflinePlayer selectedPlayer;

    public GuiPagedSinglePlayerSelector(final String title, final int maxRows, final int minRows, final UUID player, final OfflinePlayer[] players, final GuiBase prevGui) {
        super(title, maxRows, minRows, player, Arrays.stream(players).map(p -> {
            final ItemStack item = ItemUtils.getSkullItem(p);
            ItemUtils.setDisplayName(item, "§a" + p.getName());
            return item;
        }).toArray(ItemStack[]::new), prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (super.onGuiClick(event)) {
            if (selected == null || !LibraryPlugin.COMPAT.containsNBT(selected, "HEAD_UUID")) {
                selected = null;
                selectedPlayer = null;
                return true;
            }

            final UUID uuid = UUID.fromString(LibraryPlugin.COMPAT.getNBTString(selected, "HEAD_UUID"));
            selectedPlayer = Bukkit.getOfflinePlayer(uuid);
            return true;
        }
        return false;
    }
}
