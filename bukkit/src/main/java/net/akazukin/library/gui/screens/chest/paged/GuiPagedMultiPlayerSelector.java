package net.akazukin.library.gui.screens.chest.paged;

import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class GuiPagedMultiPlayerSelector extends GuiPagedMultiSelectorBase {
    protected OfflinePlayer[] selectedPlayers = new OfflinePlayer[0];

    public GuiPagedMultiPlayerSelector(final String title, final int maxRows, final int minRows, final Player player,
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
            if (event.getCurrentItem() == null ||
                    event.getCurrentItem().getType() != Material.getMaterial("PLAYER_HEAD") ||
                    !LibraryPlugin.COMPAT.containsPlData(event.getCurrentItem(), "HEAD_UUID")
            ) return true;

            this.selectedPlayers = Arrays.stream(this.selected)
                    .filter(itemStack -> itemStack.getType() == Material.getMaterial("PLAYER_HEAD") &&
                            LibraryPlugin.COMPAT.containsPlData(itemStack, "HEAD_UUID"))
                    .map(item -> UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(item, "HEAD_UUID")))
                    .filter(Objects::nonNull)
                    .map(Bukkit::getOfflinePlayer)
                    .toArray(OfflinePlayer[]::new);

            return true;
        }
        return false;
    }

    @Override
    public boolean reset() {
        final boolean result = super.reset() && this.selectedPlayers.length != 0;
        this.selectedPlayers = new OfflinePlayer[0];
        return result;
    }
}
