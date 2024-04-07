package net.akazukin.library.gui.screens.chest.paged;

import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
public class GuiPagedMultiPlayerSelector extends GuiPagedMultiSelectorBase {
    protected OfflinePlayer[] selectedPlayers = new OfflinePlayer[0];

    public GuiPagedMultiPlayerSelector(final String title, final int maxRows, final int minRows, final UUID player, final OfflinePlayer[] players, final GuiBase prevGui) {
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
                    !LibraryPlugin.COMPAT.containsNBT(event.getCurrentItem(), "HEAD_UUID")
            ) return true;

            selectedPlayers = Arrays.stream(selected)
                    .filter(itemStack -> itemStack.getType() == Material.getMaterial("PLAYER_HEAD") &&
                            LibraryPlugin.COMPAT.containsNBT(itemStack, "HEAD_UUID"))
                    .map(item -> StringUtils.toUuid(LibraryPlugin.COMPAT.getNBTString(item, "HEAD_UUID")))
                    .filter(Objects::nonNull)
                    .peek(System.out::println)
                    .map(Bukkit::getOfflinePlayer)
                    .toArray(OfflinePlayer[]::new);

            return true;
        }
        return false;
    }

    @Override
    public boolean reset() {
        final boolean result = super.reset() && selectedPlayers.length != 0;
        selectedPlayers = new OfflinePlayer[0];
        return result;
    }
}
