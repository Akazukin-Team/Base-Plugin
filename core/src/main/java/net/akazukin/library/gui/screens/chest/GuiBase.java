package net.akazukin.library.gui.screens.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.akazukin.library.event.Listenable;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class GuiBase implements Listenable {
    protected Player player;
    protected GuiBase prevGui;

    public boolean forceOpen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
