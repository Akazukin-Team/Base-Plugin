package org.akazukin.library.gui.screens.chest;

import lombok.Getter;
import lombok.Setter;
import org.akazukin.i18n.I18n;
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

import java.util.Arrays;
import java.util.Collections;

public class GuiSizeSelector extends ChestGuiBase implements IGuiSelector {
    protected final ItemStack doneItem;
    protected final ItemStack amountItem;
    @Getter
    private final int max;
    @Getter
    private final int min;
    @Setter
    private int defaultSize;
    @Getter
    private int result;
    private int size;
    @Getter
    private boolean done;

    public GuiSizeSelector(final String title, final Player player, final int min, final int max, final int defaulT,
                           final GuiBase prevGui) {
        super(title, 6, player, false, prevGui);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);

        this.defaultSize = defaulT;
        this.size = defaulT;
        this.result = defaulT;

        final ItemStack amountItem = new ItemStack(Material.getMaterial("WHITE_WOOL"));
        ItemUtils.setDisplayName(amountItem,
                LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                        I18n.of("library.gui.selector.amount", this.size)));
        ItemUtils.setLore(amountItem, Arrays.asList(
                LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player), I18n.of("library.gui.selector.max", max)),
                LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player), I18n.of("library.gui.selector.min", min))
        ));
        amountItem.setAmount(this.size);
        this.amountItem = ItemUtils.setGuiItem(amountItem);

        final ItemStack doneItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(doneItem,
                LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                        I18n.of("library.gui.selector.item.done")));
        this.doneItem = ItemUtils.setGuiItem(doneItem);

        this.done = false;
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
