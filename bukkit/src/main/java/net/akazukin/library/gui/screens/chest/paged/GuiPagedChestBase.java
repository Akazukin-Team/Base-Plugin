package net.akazukin.library.gui.screens.chest.paged;

import java.util.Arrays;
import java.util.UUID;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.screens.chest.ChestGuiBase;
import net.akazukin.library.gui.screens.chest.GuiBase;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.manager.BukkitMessageHelper;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GuiPagedChestBase extends ChestGuiBase {
    protected final int maxRows;
    protected final int minRows;
    protected final ItemStack[] itemStacks;
    protected final ItemStack prevPageItem;
    protected final ItemStack nextPageItem;
    protected int page = 0;

    public GuiPagedChestBase(final String title, final int maxRows, final int minRows, final Player player,
                             final ItemStack[] itemStacks, final GuiBase prevGui) {
        super(title, Math.max(maxRows, minRows), player, false, prevGui);
        this.itemStacks = Arrays.stream(itemStacks).map(item -> {
            item = ItemUtils.setGuiItem(item);
            return LibraryPlugin.COMPAT.setPlData(item, "AKZ_GUI_ITEM_UUID", String.valueOf(UUID.randomUUID()));
        }).toArray(ItemStack[]::new);
        this.maxRows = Math.max(maxRows, minRows);
        this.minRows = Math.min(maxRows, minRows);

        final ItemStack prevPageItem_ = new ItemStack(Material.getMaterial("ARROW"));
        ItemUtils.setDisplayName(prevPageItem_,
                LibraryPlugin.getPlugin().getMessageHelper().get(
                        BukkitMessageHelper.getLocale(player),
                        I18n.of("library.gui.paged.item.previous")
                ));
        this.prevPageItem = ItemUtils.setGuiItem(prevPageItem_);

        final ItemStack nextPageItem_ = new ItemStack(Material.getMaterial("ARROW"));

        ItemUtils.setDisplayName(nextPageItem_, LibraryPlugin.getPlugin().getMessageHelper().get(
                BukkitMessageHelper.getLocale(player),
                I18n.of("library.gui.paged.item.next")
        ));
        this.nextPageItem = ItemUtils.setGuiItem(nextPageItem_);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (this.prevPageItem.equals(event.getCurrentItem())) {
            if (this.page > 0) {
                this.page--;
                this.player.getOpenInventory().getTopInventory().setContents(this.getInventory().getContents());
            }
            return true;
        } else if (this.nextPageItem.equals(event.getCurrentItem())) {
            if (((this.page + 1) * 4 * 7) < this.itemStacks.length) {
                this.page++;
                this.player.getOpenInventory().getTopInventory().setContents(this.getInventory().getContents());
            }
            return true;
        }
        return false;
    }

    @Override
    protected Inventory getInventory() {
        final int itemLeft = (this.itemStacks.length / (4 * 7));
        this.rows = itemLeft >= (7 * (this.maxRows - 2)) ? this.maxRows :
                Math.max((int) Math.ceil((double) itemLeft / 7), this.minRows);

        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, BukkitMessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, BukkitMessageHelper.getLocale(this.player));
        if (this.prevGui != null)
            InventoryUtils.fillBackItem(inv, BukkitMessageHelper.getLocale(this.player));

        inv.setItem(17, this.prevPageItem);
        inv.setItem(26, this.nextPageItem);

        for (int i = 0; i < Math.min(28, this.itemStacks.length); i++) {
            inv.setItem(i + 10 + ((i / 7) * 2), this.itemStacks[(this.page * 28) + i].clone());
        }
        return inv;
    }
}
