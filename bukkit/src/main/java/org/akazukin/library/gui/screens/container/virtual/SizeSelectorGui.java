package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
import org.akazukin.util.utils.MathUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SizeSelectorGui extends MappedSelectorGui<Integer> {
    protected final ItemStack[] increaseItems;
    protected final ItemStack[] decreaseItems;
    protected final ItemStack amountItem;
    @Getter
    final
    int defaults;
    @Getter
    int max;
    @Getter
    int min;
    @Getter
    int result = -1;
    @Setter
    int size;
    @Getter
    boolean done;

    public SizeSelectorGui(final II18nLang locale, @Nullable final String title, final int max, final int min, final int defaults, final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, 4, locale, prevGui, containerMgr, guiMgr);

        this.max = max;
        this.min = min;
        this.defaults = defaults;
        this.size = defaults;

        {
            final int absMax = Math.max(Math.abs(this.min), Math.abs(max));
            final int increases = MathUtils.clamp((int) Math.ceil((double) absMax / 8), 1, 7);
            this.increaseItems = new ItemStack[increases];
            this.decreaseItems = new ItemStack[increases];
        }

        final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();

        {
            this.amountItem = new ItemStack.Builder()
                    .type(ItemTypes.WHITE_WOOL)
                    .amount(this.size)
                    .build();

            {
                final String maxText = I18n.of("library.gui.selector.max", this.max).buildRequiredWithFallback(fmt, locale);
                final String minText = I18n.of("library.gui.selector.min", this.min).buildRequiredWithFallback(fmt, locale);
                PacketItemUtils.setLore(this.amountItem, new Component[]{
                        Component.text(maxText),
                        Component.text(minText)
                });
            }

            PacketItemUtils.setCustomData(this.amountItem,
                    new String[]{"akz", "gui", "type"},
                    new NBTString("custom"));
            PacketItemUtils.setCustomData(this.amountItem,
                    new String[]{"akz", "gui", "SizeSelectorGui", "type"},
                    new NBTString("count"));
        }

        for (int i = 0; i < this.increaseItems.length; i++) {
            final int amount = 1 << (i * 3);

            {
                this.increaseItems[i] = new ItemStack.Builder()
                        .type(ItemTypes.WATER_BUCKET)
                        .build();

                PacketItemUtils.setDisplayName(this.increaseItems[i], Component.text("§a" + amount));

                {
                    final String maxText = I18n.of("library.gui.selector.size.max", this.max).buildRequiredWithFallback(fmt, locale);
                    PacketItemUtils.setLore(this.increaseItems[i], new Component[]{
                            Component.text(maxText)
                    });
                }

                PacketItemUtils.setCustomData(this.increaseItems[i],
                        new String[]{"akz", "gui", "type"},
                        new NBTString("custom"));
                PacketItemUtils.setCustomData(this.increaseItems[i],
                        new String[]{"akz", "gui", "SizeSelectorGui", "type"},
                        new NBTString("amount"));
                PacketItemUtils.setCustomData(this.increaseItems[i],
                        new String[]{"akz", "gui", "SizeSelectorGui", "amount"},
                        new NBTInt(amount));
            }

            {
                this.decreaseItems[i] = new ItemStack.Builder()
                        .type(ItemTypes.LAVA_BUCKET)
                        .build();

                PacketItemUtils.setDisplayName(this.decreaseItems[i], Component.text(ChatColor.RED.toString() + (-amount)));

                {
                    final String minText = I18n.of("library.gui.selector.size.min", this.max).buildRequiredWithFallback(fmt, locale);
                    PacketItemUtils.setLore(this.decreaseItems[i], new Component[]{
                            Component.text(minText)
                    });
                }

                PacketItemUtils.setCustomData(this.decreaseItems[i],
                        new String[]{"akz", "gui", "type"}, new NBTString("custom"));
                PacketItemUtils.setCustomData(this.decreaseItems[i],
                        new String[]{"akz", "gui", "SizeSelectorGui", "type"},
                        new NBTString("amount"));
                PacketItemUtils.setCustomData(this.decreaseItems[i],
                        new String[]{"akz", "gui", "SizeSelectorGui", "amount"},
                        new NBTInt(-amount));
            }
        }
    }

    @Override
    protected Integer[] getSelectableElements() {
        final int absMax = Math.max(Math.abs(this.min), Math.abs(this.max));
        final int increases = MathUtils.clamp((int) Math.ceil((double) absMax / 8), 1, 7);
        final Integer[] elems = new Integer[increases * 2];
        for (int i = 0; i < increases; i++) {
            final int amount = 1 << (i * 3);
            elems[i * 2] = amount;
            elems[i * 2 + 1] = -amount;
        }
        return elems;
    }

    @Override
    protected ItemStack toItemStack(final Integer element) {
        final ItemStack item;
        if (element > 0) {
            item = new ItemStack.Builder()
                    .type(ItemTypes.WATER_BUCKET)
                    .build();

            PacketItemUtils.setDisplayName(item, Component.text(ChatColor.GREEN + "+" + element));
        } else {
            item = new ItemStack.Builder()
                    .type(ItemTypes.LAVA_BUCKET)
                    .build();

            PacketItemUtils.setDisplayName(item, Component.text(ChatColor.RED.toString() + (-element)));
        }

        PacketItemUtils.setCustomData(item,
                new String[]{"akz", "gui", "type"},
                new NBTString("custom"));
        PacketItemUtils.setCustomData(item,
                new String[]{"akz", "gui", "SizeSelectorGui", "type"},
                new NBTString("amount"));
        PacketItemUtils.setCustomData(item,
                new String[]{"akz", "gui", "SizeSelectorGui", "amount"},
                new NBTInt(element));

        return item;
    }

    @Override
    protected Integer convertToElement(final ItemStack itemStack) {
        final NBTInt amountNbt = PacketItemUtils.getNBT(itemStack,
                new String[]{"akz", "gui", "SizeSelectorGui", "amount"}, NBTInt.class);
        return amountNbt != null ? amountNbt.getAsInt() : null;
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
    public boolean reset() {
        final boolean result = this.defaults != this.size;
        this.done = false;
        this.result = this.defaults;
        this.size = this.defaults;
        return result;
    }

    @Override
    protected ItemStack[] updateContents() {
        final ItemStack[] contents = super.updateContents();


        this.amountItem.setAmount(this.size);
        {
            final II18nFormatter fmt = LibraryPlugin.getPlugin().getI18nManager().getFormatter();
            final String nameText = I18n.of("library.gui.selector.amount", this.size).buildRequiredWithFallback(fmt, this.locale);
            PacketItemUtils.setDisplayName(this.amountItem, Component.text(nameText));
        }
        contents[13] = this.amountItem;

        {
            final ItemStack[] stacks = this.updateSelectableElements();
            final int rows = (stacks.length / 2) / 7;
            int index = (9 * 2) + 1;
            for (int i = 0; i < stacks.length; i += 2) {
                contents[index] = stacks[i];
                contents[index + (9 * rows)] = stacks[i + 1];

                index++;
            }
        }

        return contents;
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack itemStack) {
        return super.onClick(event, user, itemStack);
    }

    @Override
    public boolean isSelected(final Integer value) {
        return false;
    }

    @Override
    protected void onUnselection(final User user, final Integer value) {
    }

    @Override
    protected void onSelection(final User user, final Integer value) {
        this.size = MathUtils.clamp(this.size + value, this.min, this.max);
    }
}
