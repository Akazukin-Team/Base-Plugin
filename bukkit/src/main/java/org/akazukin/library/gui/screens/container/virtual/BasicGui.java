package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.AccessLevel;
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
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BasicGui extends VirtualChestGui {
    protected final II18nLang locale;
    protected final ItemStack closeItem;
    protected final ItemStack backItem;
    protected boolean openPrev;

    protected BasicGui(@Nullable final String title, final int rows, final II18nLang locale, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, prevGui, containerMgr, guiMgr);

        this.locale = locale;

        final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();

        {
            this.closeItem = new ItemStack.Builder()
                    .type(ItemTypes.BARRIER)
                    .amount(1)
                    .build();

            PacketItemUtils.setDisplayName(this.closeItem,
                    Component.text(I18n.of("library.gui.item.close").buildRequiredWithFallback(fmt, locale)));
            PacketItemUtils.setCustomData(this.closeItem, new String[]{"akz", "gui", "type"}, new NBTString("close"));
        }

        {
            this.backItem = new ItemStack.Builder()
                    .type(ItemTypes.IRON_DOOR)
                    .amount(1)
                    .build();

            PacketItemUtils.setDisplayName(this.backItem,
                    Component.text(I18n.of("library.gui.item.back").buildRequiredWithFallback(fmt, locale)));
            PacketItemUtils.setCustomData(this.backItem, new String[]{"akz", "gui", "type"}, new NBTString("back"));
        }
    }

    protected ItemStack[] updateContents() {
        final ItemStack[] contents = new ItemStack[this.inv.getContents().length];

        {
            final ItemStack item = ItemStack.builder().type(ItemTypes.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .build();

            PacketItemUtils.setDisplayName(item, Component.text(ChatColor.GRAY + "None"));

            Arrays.fill(contents, item);
        }


        contents[contents.length - 9] = this.closeItem;
        if (this.prevGui != null) {
            contents[contents.length - 8] = this.backItem;
        }

        return contents;
    }

    @Override
    public void onClose(final UUID player) {
        if (this.openPrev) {
            this.guiMgr.openGui(player, this.prevGui);
        }
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack clickedItem) {
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
        if (Objects.equals(type, "back")) {
            this.openPrev = true;
            this.guiMgr.closeGui(user.getUUID(), this);
            return true;
        } else if (Objects.equals(type, "close")) {
            this.openPrev = false;
            this.guiMgr.closeGui(user.getUUID(), this);
            return true;
        }
        return false;
    }
}
