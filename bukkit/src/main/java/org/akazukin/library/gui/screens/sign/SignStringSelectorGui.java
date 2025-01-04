package org.akazukin.library.gui.screens.sign;

import lombok.Getter;
import org.akazukin.library.LibraryPlugin;
import org.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import org.akazukin.library.compat.minecraft.data.packets.SBlockChangePacket;
import org.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import org.akazukin.library.event.events.PacketReceiveEvent;
import org.akazukin.library.gui.screens.chest.GuiBase;
import org.akazukin.library.worldedit.Vec3i;
import org.bukkit.Location;
import org.bukkit.Material;
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
        final Location loc = this.player.getLocation().add(0, 0, 0);

        final Vec3i pos = new Vec3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        LibraryPlugin.getPlugin().getCompat().sendPacket(this.player, new SBlockChangePacket(pos, Material.OAK_SIGN.createBlockData()));
        LibraryPlugin.getPlugin().getCompat().sendSignUpdate(this.player, loc, Arrays.copyOf(this.result, 4));
        LibraryPlugin.getPlugin().getCompat().sendPacket(this.player, new COpenSignEditorPacket(pos, true));
        return true;
    }

    public void onGuiOpen() {
    }

    public void onGuiClose(final PacketReceiveEvent event) {
        final SUpdateSignPacket pkt = (SUpdateSignPacket) LibraryPlugin.getPlugin().getCompat().getWrappedPacket(event.getPacket());
        LibraryPlugin.getPlugin().getCompat().sendPacket(this.player,
                new SBlockChangePacket(
                        pkt.getPosition(),
                        LibraryPlugin.getPlugin().getCompat()
                                .getBlockData(
                                        this.player.getWorld().getBlockAt(
                                                        pkt.getPosition().getX(),
                                                        pkt.getPosition().getY(),
                                                        pkt.getPosition().getZ())
                                                .getState())));
        this.result = pkt.getLines();
    }

    public boolean reset() {
        final boolean isNotNull = this.result != null;
        this.result = new String[]{"", null, null, null};
        return isNotNull;
    }
}
