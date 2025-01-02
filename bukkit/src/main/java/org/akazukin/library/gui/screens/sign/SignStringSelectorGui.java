package org.akazukin.library.gui.screens.sign;

import lombok.Getter;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import org.akazukin.library.event.events.PacketReceiveEvent;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.worldedit.Vec3i;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
public class SignStringSelectorGui extends GuiBase {
    protected String[] result = new String[]{"", null, null, null};

    public SignStringSelectorGui(final Player player, final GuiBase prevGui) {
        super(player, prevGui);
    }

    @Override
    public boolean forceOpen() {
        if (this.player == null) {
            return false;
        }
        Bukkit.getWorlds().get(0).getMaxHeight();
        LibraryPlugin.getPlugin().getCompat().sendSignUpdate(this.player, this.player.getLocation(), Arrays.copyOf(this.result, 4));
        LibraryPlugin.getPlugin().getCompat().sendPacket(
                this.player,
                new COpenSignEditorPacket(
                        new Vec3i(this.player.getLocation().getBlockX(), this.player.getLocation().getBlockY(),
                                this.player.getLocation().getBlockZ()),
                        true
                )
        );
        return true;
    }

    public void onGuiOpen() {
    }

    public void onGuiClose(final PacketReceiveEvent event) {
        final SUpdateSignPacket pkt = (SUpdateSignPacket) LibraryPlugin.getPlugin().getCompat().getWrappedPacket(event.getPacket());
        this.result = pkt.getLines();
    }

    public boolean reset() {
        final boolean isNotNull = this.result != null;
        this.result = new String[]{"", null, null, null};
        return isNotNull;
    }
}
