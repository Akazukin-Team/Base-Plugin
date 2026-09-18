package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import net.kyori.adventure.text.Component;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.utils.PacketItemUtils;

public abstract class PagedSelectorGui<T> extends MappedSelectorGui<T> {
    protected final ItemStack prevPageItem;
    protected final ItemStack nextPageItem;
    protected int page;

    protected PagedSelectorGui(final String title, final int rows, final II18nLang locale,
                               final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);
        final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();

        this.prevPageItem = ItemStack.builder()
                .type(ItemTypes.ARROW)
                .build();
        PacketItemUtils.setDisplayName(this.prevPageItem,
                Component.text(I18n.of("library.gui.paged.item.previous").buildRequiredWithFallback(fmt, locale)));
        PacketItemUtils.setCustomData(this.prevPageItem, new String[]{"akz", "gui", "type"}, new NBTString("custom"));
        PacketItemUtils.setCustomData(this.prevPageItem,
                new String[]{"akz", "gui", "PagedGui", "type"}, new NBTString("prev"));

        this.nextPageItem = ItemStack.builder()
                .type(ItemTypes.ARROW)
                .build();
        PacketItemUtils.setDisplayName(this.nextPageItem,
                Component.text(I18n.of("library.gui.paged.item.next").buildRequiredWithFallback(fmt, locale)));
        PacketItemUtils.setCustomData(this.nextPageItem,
                new String[]{"akz", "gui", "PagedGui", "type"}, new NBTString("next"));
    }

    @Override
    protected ItemStack[] updateContents() {
        final ItemStack[] contents = super.updateContents();

        contents[contents.length - (9 * 2)] = this.prevPageItem;
        contents[contents.length - 9] = this.nextPageItem;

        final ItemStack[] items = this.updateSelectableElements();
        for (int i = 0; i < Math.min(28, items.length); i++) {
            contents[i + 10 + ((i / 7) * 2)] = items[(this.page * 28) + i];
        }

        return contents;
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack clickedItem) {
        if (super.onClick(event, user, clickedItem)) {
            return true;
        }

        if (this.prevPageItem.equals(clickedItem)) {
            if (this.page > 0) {
                this.page--;
                this.refreshContainer();
            }
            return true;
        } else if (this.nextPageItem.equals(clickedItem)) {
            this.page++;
            this.refreshContainer();
            return true;
        }

        return false;
    }
}
