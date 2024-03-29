package net.akazukin.library.gui;

import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.CUpdateSignPacket;
import net.akazukin.library.compat.minecraft.data.packets.SOpenSignEditorPacket;
import net.akazukin.library.event.events.PacketReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class SignGui extends GuiBase {
    protected String[] result;

    public SignGui(final UUID player, final GuiBase prevGui) {
        super(player, prevGui);
    }

    /*public void onSignChange(final SignChangeEvent event) {
        result = event.getLines();
    }*/

    @Override
    public boolean forceOpen() {
        final Player player_ = Bukkit.getPlayer(player);
        if (player_ == null) return false;
        Bukkit.getWorlds().get(0).getMaxHeight();
        LibraryPlugin.COMPAT.sendSignUpdate(player_, player_.getLocation(), result);
        LibraryPlugin.COMPAT.sendPacket(
                player_,
                new SOpenSignEditorPacket(
                        new WrappedBlockPos(player_.getLocation().getBlockX(), player_.getLocation().getBlockY(), player_.getLocation().getBlockZ()),
                        true
                )
        );
        return true;
    }

    public void onGuiOpen() {
    }

    public void onGuiClose(final PacketReceiveEvent event) {
        final CUpdateSignPacket pkt = (CUpdateSignPacket) LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
        result = pkt.getLines();
    }
}
