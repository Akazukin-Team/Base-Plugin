package org.akazukin.library.gui.screens.container.virtual;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Getter;
import lombok.Setter;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.container.VirtualContainerManager;
import org.akazukin.library.gui.IGui;
import org.akazukin.library.gui.IGuiManager;

@Getter
@Setter
public abstract class PagedSingleSelectorGui<T> extends PagedSelectorGui<T> {
    protected T selected;

    protected PagedSingleSelectorGui(final String title, final int rows, final II18nLang locale, final IGui prevGui,
                                     final VirtualContainerManager containerMgr, final IGuiManager guiMgr) {
        super(title, rows, locale, prevGui, containerMgr, guiMgr);
    }

    @Override
    public boolean onClick(final WrapperPlayClientClickWindow event, final User user, final ItemStack clickedItem) {
        return super.onClick(event, user, clickedItem);
    }

    @Override
    public boolean isDone() {
        return this.selected != null;
    }

    @Override
    public boolean reset() {
        final boolean result = this.selected != null;
        this.selected = null;
        return result;
    }

    @Override
    public boolean isSelected(final T element) {
        return element.equals(this.selected);
    }

    @Override
    protected void onUnselection(final User user, final T element) {
        if (element.equals(this.selected)) {
            this.selected = null;
        }
    }

    @Override
    protected void onSelection(final User user, final T element) {
        this.selected = element;
    }
}
