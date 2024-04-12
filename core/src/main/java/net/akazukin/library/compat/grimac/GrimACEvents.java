package net.akazukin.library.compat.grimac;

import ac.grim.grimac.api.events.FlagEvent;
import net.akazukin.library.event.EventTarget;
import net.akazukin.library.event.Listenable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

public class GrimACEvents implements Listenable {
    private final boolean handleEvents;

    public GrimACEvents() {
        handleEvents = Bukkit.getPluginManager().isPluginEnabled("GrimAC");
    }

    @EventTarget(bktPriority = EventPriority.HIGH)
    public void onACDetect(final FlagEvent event) {
        System.out.println(event.getCheck().getCheckName());
    }

    @Override
    public boolean handleEvents() {
        return handleEvents;
    }
}
