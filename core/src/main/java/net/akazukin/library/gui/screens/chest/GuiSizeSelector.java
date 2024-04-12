package net.akazukin.library.gui.screens.chest;

import lombok.Getter;
import lombok.Setter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.gui.screens.chest.paged.IGuiSelector;
import net.akazukin.library.i18n.I18n;
import net.akazukin.library.utils.InventoryUtils;
import net.akazukin.library.utils.ItemUtils;
import net.akazukin.library.utils.MessageHelper;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

public class GuiSizeSelector extends ChestGuiBase implements IGuiSelector {
    @Getter
    private final int max;
    @Getter
    private final int min;

    @Setter
    private int defaultSize;
    protected final ItemStack doneItem;

    @Getter
    private int result;
    private int size;
    protected final ItemStack amountItem;

    @Getter
    private boolean done;

    public GuiSizeSelector(final String title, final UUID player, final int min, final int max, final int defaulT, final GuiBase prevGui) {
        super(title, 6, player, false, prevGui);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);

        defaultSize = defaulT;
        size = defaulT;
        result = defaulT;

        final ItemStack amountItem_ = new ItemStack(Material.getMaterial("WHITE_WOOL"));
        ItemUtils.setDisplayName(amountItem_, "§aAmount: " + size);
        amountItem_.setAmount(size);
        amountItem = ItemUtils.setGuiItem(amountItem_);

        final ItemStack doneItem_ = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(doneItem_, LibraryPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("library.gui.paged.selector.item.done")));
        doneItem = ItemUtils.setGuiItem(doneItem_);

        done = false;
    }

    @Override
    public Inventory getInventory() {
        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(player));
        if (prevGui != null)
            InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(player));

        inv.setItem(13, amountItem);


        ItemStack increaseItem = new ItemStack(Material.getMaterial("WATER_BUCKET"));
        ItemUtils.setLore(increaseItem, Collections.singletonList(LibraryPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("library.gui.selector.size.max"), max)));
        increaseItem = ItemUtils.setGuiItem(increaseItem);

        ItemStack decreaseItem = new ItemStack(Material.getMaterial("LAVA_BUCKET"));
        ItemUtils.setLore(decreaseItem, Collections.singletonList(LibraryPlugin.MESSAGE_HELPER.get(MessageHelper.getLocale(player), I18n.of("library.gui.selector.size.min"), max)));
        decreaseItem = ItemUtils.setGuiItem(decreaseItem);

        int index = 19;
        for (int i = 1; i <= max; i = i * 4) {
            if (min > i) continue;

            ItemUtils.setDisplayName(increaseItem, "§a" + i);
            increaseItem.setAmount(i);
            inv.setItem(index, increaseItem);

            ItemUtils.setDisplayName(decreaseItem, "§c" + i);
            decreaseItem.setAmount(i);
            inv.setItem(index + 9, decreaseItem);

            index++;
        }
        inv.setItem(inv.getSize() - 7, doneItem);

        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return false;

        if (event.getCurrentItem().equals(doneItem)) {
            result = size;
            GuiManager.singleton().setScreen(player, prevGui);
            return true;
        } else if (event.getCurrentItem().getType() == Material.getMaterial("WATER_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (size >= max) {
                LibraryPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("library.gui.selector.size.cantIncrease"));
            } else {
                size += amount;
                if (size > max) size = max;

                if (event.getView().getItem(13).equals(amountItem)) {
                    ItemUtils.setDisplayName(amountItem, "§aAmount: " + size);
                    amountItem.setAmount(size);
                    event.getView().setItem(13, amountItem);
                }
            }
            return true;
        } else if (event.getCurrentItem().getType() == Material.getMaterial("LAVA_BUCKET") &&
                event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§c")) {
            final int amount = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
            if (size <= min) {
                LibraryPlugin.MESSAGE_HELPER.sendMessage(event.getWhoClicked(), I18n.of("library.gui.selector.size.cantDecrease"));
            } else {
                size -= amount;
                if (size < min) size = min;

                if (event.getView().getItem(13).equals(amountItem)) {
                    ItemUtils.setDisplayName(amountItem, "§aAmount: " + size);
                    amountItem.setAmount(size);
                    event.getView().setItem(13, amountItem);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean reset() {
        final boolean result = defaultSize != size;
        done = false;
        this.result = defaultSize;
        size = defaultSize;
        return result;
    }
}
