package net.akazukin.library.gui.screens.sign;

import java.util.Arrays;
import java.util.UUID;
import lombok.Getter;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.compat.minecraft.data.WrappedBlockPos;
import net.akazukin.library.compat.minecraft.data.packets.COpenSignEditorPacket;
import net.akazukin.library.compat.minecraft.data.packets.SUpdateSignPacket;
import net.akazukin.library.event.events.PacketReceiveEvent;
import net.akazukin.library.gui.screens.chest.GuiBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class SignStringSelectorGui extends GuiBase {
    protected String[] result = new String[]{"", null, null, null};

    public SignStringSelectorGui(final UUID player, final GuiBase prevGui) {
        super(player, prevGui);
    }

    @Override
    public boolean forceOpen() {
        final Player player_ = Bukkit.getPlayer(player);
        if (player_ == null) return false;
        Bukkit.getWorlds().get(0).getMaxHeight();
        LibraryPlugin.COMPAT.sendSignUpdate(player_, player_.getLocation(), Arrays.copyOf(result, 4));
        LibraryPlugin.COMPAT.sendPacket(
                player_,
                new COpenSignEditorPacket(
                        new WrappedBlockPos(player_.getLocation().getBlockX(), player_.getLocation().getBlockY(),
                                player_.getLocation().getBlockZ()),
                        true
                )
        );
        return true;
    }

    public void onGuiOpen() {
    }

    public void onGuiClose(final PacketReceiveEvent event) {
        final SUpdateSignPacket pkt = (SUpdateSignPacket) LibraryPlugin.COMPAT.getWrappedPacket(event.getPacket());
        result = pkt.getLines();
    }

    public boolean reset() {
        final boolean isNotNull = result != null;
        result = new String[]{"", null, null, null};
        return isNotNull;
    }
}
