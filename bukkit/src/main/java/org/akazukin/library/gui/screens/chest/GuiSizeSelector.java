package org.akazukin.library.gui.screens.chest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.object.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.paged.IGuiSelector;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.InventoryUtils;
import org.akazukin.library.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GuiSizeSelector extends ChestGuiBase implements IGuiSelector {
    protected final ItemStack doneItem;
    protected final ItemStack amountItem;
    @Getter
    int max;
    @Getter
    int min;
    @Getter
    int defaultSize;
    @Getter
    int result = -1;
    @Setter
    int size;
    @Getter
    boolean done;

    public GuiSizeSelector(final String title, final Player player,
                           final GuiBase prevGui) {
        super(title, 6, player, false, prevGui);

        final ItemStack amountItem = new ItemStack(Material.getMaterial("WHITE_WOOL"));
        ItemUtils.setDisplayName(amountItem,
                I18n.of("library.gui.selector.amount", this.size).buildRequiredWithFallback(
                        LibraryPlugin.getPlugin().getI18nManager().getFormatter(),
                        BukkitMessageHelper.getLocale(player)));
        ItemUtils.setLore(amountItem,
                new String[]{
                        I18n.of("library.gui.selector.max", this.max).buildRequiredWithFallback(
                                LibraryPlugin.getPlugin().getI18nManager().getFormatter(),
                                BukkitMessageHelper.getLocale(player)),
                        I18n.of("library.gui.selector.min", this.min).buildRequiredWithFallback(
                                LibraryPlugin.getPlugin().getI18nManager().getFormatter(),
                                BukkitMessageHelper.getLocale(player))
                }
        );
        this.amountItem = ItemUtils.setGuiItem(amountItem);

        final ItemStack doneItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(doneItem,
                I18n.of("library.gui.selector.item.done").buildRequiredWithFallback(
                        LibraryPlugin.getPlugin().getI18nManager().getFormatter(),
                        BukkitMessageHelper.getLocale(player)));
        this.doneItem = ItemUtils.setGuiItem(doneItem);
    }

    public void setMax(final int max) {
        this.max = max;
        this.min = Math.min(this.min, max);
    }

    public void setMin(final int min) {
        this.min = min;
        this.max = Math.max(this.min, this.max);
    }

    @Override
    public Inventory getInventory() {
        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, BukkitMessageHelper.getLocale(this.player));
        InventoryUtils.fillCloseItem(inv, BukkitMessageHelper.getLocale(this.player));
        if (this.prevGui != null) {
            InventoryUtils.fillBackItem(inv, BukkitMessageHelper.getLocale(this.player));
        }

        inv.setItem(13, this.amountItem);


        ItemStack increaseItem = new ItemStack(Material.getMaterial("WATER_BUCKET"));
        ItemUtils.setLore(increaseItem,
                Collections.singletonList(LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                        I18n.of("library.gui.selector.size.max", this.max))));
        increaseItem = ItemUtils.setGuiItem(increaseItem);

        ItemStack decreaseItem = new ItemStack(Material.getMaterial("LAVA_BUCKET"));
        ItemUtils.setLore(decreaseItem,
                Collections.singletonList(LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                        I18n.of("library.gui.selector.size.min", this.max))));
        decreaseItem = ItemUtils.setGuiItem(decreaseItem);

        int index = 19;
        for (int i = 1; i <= this.max; i = i * 4) {
            if (this.min > i) {
                continue;
            }

            ItemUtils.setDisplayName(increaseItem, "§a" + i);
            increaseItem.setAmount(i);
            inv.setItem(index, increaseItem);

            ItemUtils.setDisplayName(decreaseItem, "§c" + i);
            decreaseItem.setAmount(i);
            inv.setItem(index + 9, decreaseItem);

            index++;
        }
        inv.setItem(inv.getSize() - 7, this.doneItem);

        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return false;
        }

        if (event.getCurrentItem().equals(this.doneItem)) {
            this.result = this.size;
            GuiManager.singleton().setScreen(this.player, () -> this.prevGui);
            return true;
        } else if (event.getCurrentItem().getType() == Material.getMaterial("WATER_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (this.size >= this.max) {
                LibraryPlugin.getPlugin().getMessageHelper().sendMessage(event.getWhoClicked(),
                        I18n.of("library.gui.selector.size.cantIncrease"));
            } else {
                this.size += amount;
                if (this.size > this.max) {
                    this.size = this.max;
                }

                if (this.amountItem.equals(event.getView().getItem(13))) {
                    ItemUtils.setDisplayName(this.amountItem,
                            LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player), I18n.of(
                                    "library" +
                                            ".gui.selector.amount", this.size)));
                    this.amountItem.setAmount(this.size);
                    event.getView().setItem(13, this.amountItem);
                }
            }
            return true;
        } else if (event.getCurrentItem().getType() == Material.getMaterial("LAVA_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§c")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (this.size <= this.min) {
                LibraryPlugin.getPlugin().getMessageHelper().sendMessage(event.getWhoClicked(),
                        I18n.of("library.gui.selector.size.cantDecrease"));
            } else {
                this.size -= amount;
                if (this.size < this.min) {
                    this.size = this.min;
                }

                if (this.amountItem.equals(event.getView().getItem(13))) {
                    ItemUtils.setDisplayName(this.amountItem,
                            LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                                    I18n.of("library.gui.selector.amount", this.size)));
                    this.amountItem.setAmount(this.size);
                    event.getView().setItem(13, this.amountItem);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean reset() {
        final boolean result = this.defaultSize != this.size;
        this.done = false;
        this.result = this.defaultSize;
        this.size = this.defaultSize;
        return result;
    }
}
