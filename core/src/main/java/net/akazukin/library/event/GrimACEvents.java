package net.akazukin.library.event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

@Getter
public class GrimACEvents implements Listener {
    private final boolean handleEvents;

    public GrimACEvents() {
        this.handleEvents = Bukkit.getPluginManager().isPluginEnabled("GrimAC");
    }
}
