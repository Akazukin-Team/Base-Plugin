package org.akazukin.library.gui.screens.chest.paged;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.Getter;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.i18n.I18n;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.utils.ItemUtils;
import org.akazukin.library.utils.UUIDUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiPagedMultiSelectorBase extends GuiPagedChestBase implements IGuiSelector {
    @Nonnull
    protected final List<UUID> selectedUuid = new ArrayList<>();
    @Nonnull
    protected final ItemStack doneItem;
    @Getter
    @Nonnull
    protected ItemStack[] selected = new ItemStack[0];
    @Getter
    private boolean done = false;

    public GuiPagedMultiSelectorBase(final String title, final int maxRows, final int minRows, final Player player,
                                     @Nonnull final ItemStack[] itemStacks, @Nonnull final GuiBase prevGui) {
        super(title, maxRows, minRows, player,
                Arrays.stream(itemStacks).map(ItemUtils::setGuiItem).toArray(ItemStack[]::new), prevGui);

        final ItemStack doneItem_ = new ItemStack(Material.getMaterial("LIME_WOOL"));
        ItemUtils.setDisplayName(doneItem_,
                LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(player),
                        I18n.of("library.gui.selector.item.done")));
        this.doneItem = ItemUtils.setGuiItem(doneItem_);
    }

    @Override
    protected boolean onGuiClick(final InventoryClickEvent event) {
        if (super.onGuiClick(event)) return true;

        if (event.getCurrentItem() == null) return false;

        if (this.doneItem.equals(event.getCurrentItem())) {
            this.done = true;
            GuiManager.singleton().setScreen(event.getWhoClicked(), () -> this.prevGui);
            return true;
        } else if (LibraryPlugin.COMPAT.containsPlData(event.getCurrentItem(), "AKZ_GUI_ITEM_UUID")) {
            if (!this.selectedUuid.contains(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(event.getCurrentItem(), "AKZ_GUI_ITEM_UUID")))) {
                this.selectedUuid.add(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(event.getCurrentItem(),
                        "AKZ_GUI_ITEM_UUID")));

                this.selected = Arrays.stream(this.itemStacks.clone())
                        .filter(item -> this.selectedUuid.contains(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(item, "AKZ_GUI_ITEM_UUID"))))
                        .map(item -> LibraryPlugin.COMPAT.removePlData(item.clone(), "AKZ_GUI_ITEM_UUID"))
                        .toArray(ItemStack[]::new);

                List<String> lore = ItemUtils.getLore(event.getCurrentItem());
                if (lore == null) lore = new ArrayList<>();
                lore.add(LibraryPlugin.getPlugin().getMessageHelper().get(BukkitMessageHelper.getLocale(this.player),
                        I18n.of(
                                "library.gui" +
                                        ".paged.selector.selected")));
                ItemUtils.setLore(event.getCurrentItem(), lore);

                return true;
            } else if (LibraryPlugin.COMPAT.containsPlData(event.getCurrentItem(), "AKZ_GUI_ITEM_UUID") &&
                    this.selectedUuid.contains(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(event.getCurrentItem(), "AKZ_GUI_ITEM_UUID")))) {
                this.selectedUuid.remove(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(event.getCurrentItem(),
                        "AKZ_GUI_ITEM_UUID")));

                this.selected = Arrays.stream(this.itemStacks)
                        .filter(item -> this.selectedUuid.contains(UUIDUtils.toUuid(LibraryPlugin.COMPAT.getPlDataString(item, "AKZ_GUI_ITEM_UUID"))))
                        .map(item -> LibraryPlugin.COMPAT.removePlData(item.clone(), "AKZ_GUI_ITEM_UUID"))
                        .toArray(ItemStack[]::new);

                Arrays.stream(this.itemStacks)
                        .filter(itemStack -> LibraryPlugin.COMPAT.containsPlData(itemStack, "AKZ_GUI_ITEM_UUID"))
                        .filter(itemStack ->
                                LibraryPlugin.COMPAT.getPlDataString(itemStack, "AKZ_GUI_ITEM_UUID").equals(
                                        LibraryPlugin.COMPAT.getPlDataString(event.getCurrentItem(),
                                                "AKZ_GUI_ITEM_UUID")
                                ))
                        .findFirst()
                        .ifPresent(itemStack -> ItemUtils.setLore(event.getCurrentItem(),
                                ItemUtils.getLore(itemStack)));

                return true;
            }
        }
        return false;
    }

    @Override
    protected Inventory getInventory() {
        final Inventory inv = super.getInventory();
        inv.setItem(inv.getSize() - 7, this.doneItem);
        return inv;
    }

    @Override
    public boolean reset() {
        final boolean result = this.selected.length != 0 || !this.selectedUuid.isEmpty();
        this.selected = new ItemStack[0];
        this.selectedUuid.clear();
        this.done = false;
        return result;
    }
}
