package org.akazukin.library.gui.screens.chest.paged;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class GuiPagedSingleSelector extends GuiPagedChestBase implements IGuiSelector {
    protected ItemStack selected;

    public GuiPagedSingleSelector(final String title, final int maxRows, final int minRows, final Player player,
                                  final ItemStack[] itemStacks, final GuiBase prevGui) {
        super(title, maxRows, minRows, player,
                Arrays.stream(itemStacks)
                        .map(ItemUtils::setGuiItem)
                        .toArray(ItemStack[]::new),
                prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (super.onGuiClick(event)) return true;

        if (Arrays.asList(this.itemStacks).contains(event.getCurrentItem())) {
            this.selected = LibraryPlugin.COMPAT.removePlData(event.getCurrentItem().clone(), "AKZ_GUI_ITEM_UUID");
            return true;
        }

        return false;
    }

    @Override
    public boolean isDone() {
        return this.selected != null;
    }

    @Override
    public boolean reset() {
        final boolean result = this.selected != null;
        this.selected = null;
        return result;
    }
}
