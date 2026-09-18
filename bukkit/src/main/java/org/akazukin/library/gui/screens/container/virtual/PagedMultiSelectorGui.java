package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedMultiSelectorGui<T> extends PagedSelectorGui<T> {
    @Getter
    @NotNull
    protected List<T> selected = new ArrayList<>();
    @Getter
    private boolean done;

    protected PagedMultiSelectorGui(final String title, final int rows, final II18nLang locale, @NotNull final IGui prevGui,
                                    final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);
    }

    @Override
    protected ItemStack[] updateContents() {
        final ItemStack[] contents = super.updateContents();
        contents[contents.length - 7] = this.doneItem;
        return contents;
    }

    @Override
    public boolean reset() {
        final boolean result = super.reset() || !this.selected.isEmpty();
        this.selected = new ArrayList<>();
        return result;
    }

    @Override
    public boolean isSelected(final T element) {
        return this.selected.contains(element);
    }

    @Override
    protected void onUnselection(final User user, final T element) {
        this.selected.remove(element);
    }

    @Override
    protected void onSelection(final User user, final T element) {
        this.selected.add(element);
    }
}
