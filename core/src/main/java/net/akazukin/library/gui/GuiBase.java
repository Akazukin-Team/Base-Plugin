package net.akazukin.library.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.akazukin.library.event.Listenable;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class GuiBase implements Listenable {
    protected UUID player;
    protected GuiBase prevGui;

    public boolean forceOpen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
