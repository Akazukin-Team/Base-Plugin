package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.utils.IdentityHashBiMap;
import org.akazukin.util.object.Pair;

import java.util.Arrays;

public abstract class MappedSelectorGui<T> extends SelectorGui<T> {
    protected IdentityHashBiMap<ItemStack, T> elementMap;

    protected MappedSelectorGui(final String title, final int rows, final II18nLang locale,
                                final IGui prevGui, final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);
    }

    protected ItemStack[] updateSelectableElements() {
        return Arrays.stream(this.getSelectableElements())
                .map(e -> new Pair<>(this.toItemStack(e), e))
                .peek(e -> this.elementMap.put(e.getKey(), e.getValue()))
                .map(Pair::getKey)
                .toArray(ItemStack[]::new);
    }

    protected abstract T[] getSelectableElements();

    protected abstract ItemStack toItemStack(final T element);

    @Override
    protected T convertToElement(final ItemStack itemStack) {
        return this.elementMap.get(itemStack);
    }
}
