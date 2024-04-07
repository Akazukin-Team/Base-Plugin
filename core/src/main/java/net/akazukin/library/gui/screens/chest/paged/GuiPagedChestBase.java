package net.akazukin.library.gui.screens.chest.paged;

import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public abstract class GuiPagedChestBase extends ChestGuiBase {
    protected final int maxRows;
    protected final int minRows;
    protected int page = 0;
    protected final ItemStack[] itemStacks;

    protected final ItemStack prevPageItem;
    protected final ItemStack nextPageItem;

    public GuiPagedChestBase(final String title, final int maxRows, final int minRows, final UUID player, final ItemStack[] itemStacks, final GuiBase prevGui) {
        super(title, Math.max(maxRows, minRows), player, false, prevGui);
        this.itemStacks = Arrays.stream(itemStacks).map(item -> {
            item = ItemUtils.setGuiItem(item);
            return LibraryPlugin.COMPAT.setNBT(item, "AKZ_GUI_ITEM_UUID", String.valueOf(UUID.randomUUID()));
        }).toArray(ItemStack[]::new);
        this.maxRows = Math.max(maxRows, minRows);
        this.minRows = Math.min(maxRows, minRows);

        final ItemStack prevPageItem_ = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(prevPageItem_,
                LibraryPlugin.MESSAGE_HELPER.get(
                        MessageHelper.getLocale(player),
                        I18n.of("library.gui.paged.item.previous")
                ));
        prevPageItem = ItemUtils.setGuiItem(prevPageItem_);

        final ItemStack nextPageItem_ = new ItemStack(Material.getMaterial("ARROW"));

        ItemUtils.setDisplayName(nextPageItem_, LibraryPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("library.gui.paged.item.next")
        ));
        nextPageItem = ItemUtils.setGuiItem(nextPageItem_);
    }

    @Override
    protected Inventory getInventory() {
        final int itemLeft = (itemStacks.length / (4 * 7));
        rows = itemLeft >= (7 * (maxRows - 2)) ? maxRows : Math.max((int) Math.ceil((double) itemLeft / 7), minRows);

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv);
        InventoryUtils.fillCloseItem(inv);
        if (prevGui != null)
            InventoryUtils.fillPrevGuiItem(inv);

        inv.setItem(17, prevPageItem);
        inv.setItem(26, nextPageItem);

        for (int i = 0; i < Math.min(28, itemStacks.length); i++) {
            inv.setItem(i + 10 + ((i / 7) * 2), itemStacks[(page * 28) + i].clone());
        }
        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (prevPageItem.equals(event.getCurrentItem())) {
            if (page > 0) {
                page--;
                Bukkit.getPlayer(player).getOpenInventory().getTopInventory().setContents(getInventory().getContents());
            }
            return true;
        } else if (nextPageItem.equals(event.getCurrentItem())) {
            if (((page + 1) * 4 * 7) < itemStacks.length) {
                page++;
                Bukkit.getPlayer(player).getOpenInventory().getTopInventory().setContents(getInventory().getContents());
            }
            return true;
        }
        return false;
    }
}
