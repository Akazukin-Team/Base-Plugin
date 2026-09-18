package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.utils.PacketItemUtils;

public class YesOrNoGui extends MappedSelectorGui<Boolean> {
    private final ItemStack yesItem;
    private final ItemStack noItem;
    @Getter
    private Boolean result;
    @Getter
    private boolean done;

    public YesOrNoGui(final II18nLang locale, final String title, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, 4, locale, prevGui, containerMgr, guiMgr);

        final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();

        this.yesItem = ItemStack.builder().type(ItemTypes.LIME_WOOL).build();
        PacketItemUtils.setDisplayName(this.yesItem,
                Component.text(I18n.of("library.gui.yesNo.item.yes").buildRequiredWithFallback(fmt, locale)));

        this.noItem = ItemStack.builder().type(ItemTypes.RED_WOOL).build();
        PacketItemUtils.setDisplayName(this.noItem,
                Component.text(I18n.of("library.gui.yesNo.item.no").buildRequiredWithFallback(fmt, locale)));
    }

    @Override
    protected Boolean[] getSelectableElements() {
        return new Boolean[]{true, false};
    }

    @Override
    protected ItemStack toItemStack(final Boolean element) {

        if (element == null) {
            return null;
        } else if (element) {
            return this.yesItem;
        } else {
            return this.noItem;
        }
    }

    @Override
    public boolean reset() {
        final boolean rs = this.result != null;
        this.result = null;
        this.done = false;
        return rs;
    }

    @Override
    protected ItemStack[] updateContents() {
        final ItemStack[] contents = super.updateContents();

        final ItemStack[] elems = this.updateSelectableElements();
        contents[12] = elems[0];
        contents[14] = elems[1];

        return contents;
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack clickedItem) {
        if (super.onClick(event, user, clickedItem)) {
            return true;
        }
        if (clickedItem == null) {
            return false;
        }

        if (this.yesItem.equals(clickedItem)) {
            this.result = true;
            LibraryPlugin.getPlugin().getGuiMgr().openGui(user.getUUID(), this.prevGui);
        } else if (this.noItem.equals(clickedItem)) {
            this.result = false;
            LibraryPlugin.getPlugin().getGuiMgr().openGui(user.getUUID(), this.prevGui);
        }
        return false;
    }

    @Override
    public boolean isSelected(final Boolean value) {
        return this.result == value;
    }

    @Override
    protected void onUnselection(final User user, final Boolean value) {
        if (this.result == value) {
            this.result = null;
        }
    }

    @Override
    protected void onSelection(final User user, final Boolean value) {
        this.result = value;
    }
}
