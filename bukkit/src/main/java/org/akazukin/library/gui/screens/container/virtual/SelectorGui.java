package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.akazukin.i18n.manager.II18nFormatter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.i18n.object.I18n;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.utils.PacketItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SelectorGui<T> extends BasicGui implements ISelectorGui {
    protected final ItemStack doneItem;
    @Getter
    boolean done;

    protected SelectorGui(@Nullable final String title, final int rows, final II18nLang locale, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);

        final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();

        {
            this.doneItem = new ItemStack.Builder()
                    .type(ItemTypes.LIME_WOOL)
                    .build();

            PacketItemUtils.setDisplayName(this.doneItem,
                    Component.text(I18n.of("library.gui.selector.item.done").buildRequiredWithFallback(fmt, locale)));
            PacketItemUtils.setCustomData(this.doneItem, new String[]{"akz", "gui", "type"}, new NBTString("done"));
        }
    }

    @Override
    public boolean reset() {
        this.done = false;
        return true;
    }

    @Override
    protected ItemStack[] updateContents() {
        final ItemStack[] contents = super.updateContents();

        contents[contents.length - 7] = this.doneItem;

        return contents;
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack itemStack) {
        final ItemStack clickedItem = event.getCarriedItemStack();
        if (clickedItem.isEmpty()) {
            return false;
        }

        final String type;
        {
            final NBTString typeTag = PacketItemUtils.getNBT(clickedItem,
                    new String[]{"akz", "gui", "type"},
                    NBTString.class);
            if (typeTag != null) {
                type = typeTag.getValue();
            } else {
                type = null;
            }
        }

        {
            final T elem = this.convertToElement(itemStack);
            if (elem != null) {
                if (this.isSelected(elem)) {
                    this.onUnselection(user, elem);
                } else {
                    this.onSelection(user, elem);
                }
                return true;
            }
        }
        if (Objects.equals(type, "custom")) {
            final String customType;
            {
                final NBTString customTypeTag = PacketItemUtils.getNBT(clickedItem,
                        new String[]{"akz", "gui", "SelectorGui", "type"},
                        NBTString.class);
                if (customTypeTag != null) {
                    customType = customTypeTag.getValue();
                } else {
                    customType = null;
                }
            }

            if (Objects.equals(customType, "done")) {
                this.done = true;
                this.openPrev = true;
                this.guiMgr.closeGui(user.getUUID(), this);
                return true;
            } else if (Objects.equals(customType, "selectable")) {
                final T elem = this.convertToElement(clickedItem);
                if (this.isSelected(elem)) {
                    this.onUnselection(user, elem);
                } else {
                    this.onSelection(user, elem);
                }
                return true;
            }
        }
        return false;
    }

    protected abstract T convertToElement(final ItemStack itemStack);

    public abstract boolean isSelected(T value);

    protected abstract void onUnselection(User user, T value);

    protected abstract void onSelection(User user, T value);
}
