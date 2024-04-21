package net.akazukin.library.gui.screens.chest;

import java.util.UUID;
import lombok.Getter;
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

public class YesOrNoGui extends ChestGuiBase implements IGuiSelector {
    private final ItemStack yesItem;
    private final ItemStack noItem;
    @Getter
    private Boolean result;
    @Getter
    private boolean done = false;

    public YesOrNoGui(final String title, final UUID player, final GuiBase prevGui) {
        super(title, 4, player, false, prevGui);

        final ItemStack yesItem = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(yesItem, LibraryPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("library.gui.yesNo.item.yes")
        ));
        this.yesItem = ItemUtils.setGuiItem(yesItem);

        final ItemStack noItem = new ItemStack(Material.getMaterial("RED_WOOL"));
        ItemUtils.setDisplayName(noItem, LibraryPlugin.MESSAGE_HELPER.get(
                MessageHelper.getLocale(player),
                I18n.of("library.gui.yesNo.item.no")
        ));
        this.noItem = ItemUtils.setGuiItem(noItem);
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();
        InventoryUtils.fillBlankItems(inv, MessageHelper.getLocale(player));
        InventoryUtils.fillCloseItem(inv, MessageHelper.getLocale(player));
        if (prevGui != null)
            InventoryUtils.fillBackItem(inv, MessageHelper.getLocale(player));

        inv.setItem(12, yesItem);
        inv.setItem(14, noItem);

        return inv;
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        final boolean rs = super.onGuiClick(event);
        if (rs) return true;
        if (event.getCurrentItem() == null) return false;
        if (yesItem.equals(event.getCurrentItem())) {
            result = true;
            GuiManager.singleton().setScreen(player, prevGui);
        } else if (noItem.equals(event.getCurrentItem())) {
            result = false;
            GuiManager.singleton().setScreen(player, prevGui);
        }
        return false;
    }

    @Override
    public boolean reset() {
        final boolean rs = result != null;
        result = null;
        done = false;
        return rs;
    }
}
