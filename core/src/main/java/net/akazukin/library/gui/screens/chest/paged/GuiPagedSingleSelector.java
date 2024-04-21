package net.akazukin.library.gui.screens.chest.paged;

import java.util.Arrays;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.utils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class GuiPagedSingleSelector extends GuiPagedChestBase implements IGuiSelector {
    protected ItemStack selected;

    public GuiPagedSingleSelector(final String title, final int maxRows, final int minRows, final UUID player, final ItemStack[] itemStacks, final GuiBase prevGui) {
        super(title, maxRows, minRows, player,
                Arrays.stream(itemStacks)
                        .map(ItemUtils::setGuiItem)
                        .toArray(ItemStack[]::new),
                prevGui);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (super.onGuiClick(event)) return true;

        if (Arrays.asList(itemStacks).contains(event.getCurrentItem())) {
            selected = LibraryPlugin.COMPAT.removeNBT(event.getCurrentItem(), "AKZ_GUI_ITEM_UUID");
            return true;
        }

        return false;
    }

    @Override
    public boolean isDone() {
        return selected != null;
    }

    @Override
    public boolean reset() {
        final boolean result = selected != null;
        selected = null;
        return result;
    }
}
