package org.akazukin.library.gui.screens.sign;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.library.gui.IGuiManager;
import org.akazukin.library.gui.OpenStatus;
import org.akazukin.library.gui.screens.IPacketGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignGui implements IPacketGui {
    final IGuiManager guiMgr;
    final HashMap<UUID, Vector3i> poses = new HashMap<>();
    String[] results = new String[4];

    public SignGui(final IGuiManager guiMgr) {
        this.guiMgr = guiMgr;
    }

    @Override
    public OpenStatus open(final UUID player) {
        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        final Player bktPlayer = Bukkit.getPlayer(player);

        if (user == null || bktPlayer == null) {
            return OpenStatus.FAILURE;
        }

        final Location loc = bktPlayer.getLocation().add(0, 0, 0);

        final Vector3i pos = new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        // 1. フェイクブロック配置パケット (Oak Sign)
        final WrapperPlayServerBlockChange blockChangePkt = new WrapperPlayServerBlockChange(pos, StateTypes.OAK_SIGN.createBlockState());

        final NBTCompound nbt = new NBTCompound();

        // 4行分のJSONテキストをNBTListに追加
        final NBTList<NBTString> messages = new NBTList<>(NBTType.STRING, 4);
        for (int i = 0; i < 4; i++) {
            messages.addTag(i, new NBTString(this.results[i]));
        }

        final NBTCompound frontText = new NBTCompound();

        frontText.setTag("messages", messages);
        nbt.setTag("front_text", frontText);

        // 3. BlockEntityData パケットの作成
        final WrapperPlayServerBlockEntityData signDataPkt = new WrapperPlayServerBlockEntityData(
                pos,
                BlockEntityTypes.SIGN,
                nbt
        );

        // 4. 看板編集画面オープンパケット (表面: true)
        final WrapperPlayServerOpenSignEditor openEditorPkt = new WrapperPlayServerOpenSignEditor(pos, true);

        // 順序通りにパケット送信
        user.sendPacket(blockChangePkt);
        user.sendPacket(signDataPkt);
        user.sendPacket(openEditorPkt);

        this.poses.put(player, pos);

        return OpenStatus.SUCCESS_ALWAYS_OPNE;
    }

    @Override
    public void close(final UUID player) {
    }

    @Override
    public void onOpen(final UUID player) {
    }

    @Override
    public void onClose(final UUID player) {
        final Vector3i pos = this.poses.remove(player);

        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        final Player bktPlayer = Bukkit.getPlayer(player);
        if (user == null || bktPlayer == null) {
            return;
        }

        final Block block = bktPlayer.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());

        final WrappedBlockState blockState = SpigotConversionUtil.fromBukkitBlockData(block.getBlockData());
        final WrapperPlayServerBlockChange pkt = new WrapperPlayServerBlockChange(pos, blockState.getGlobalId());
        user.sendPacket(pkt);
    }

    @Override
    public void destroy() {
        new HashSet<>(this.poses.keySet()).forEach(uuid -> this.guiMgr.closeGui(uuid, this));
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            final WrapperPlayClientUpdateSign pkt = new WrapperPlayClientUpdateSign(event);
            this.onUpdate(event.getUser().getUUID(), pkt.getBlockPosition(), pkt.getTextLines());
            this.guiMgr.handleCloseGui(event.getUser().getUUID(), this);
        }
    }

    protected void onUpdate(final UUID uuid, final Vector3i blockPosition, final String[] textLines) {
        final Vector3i pos = this.poses.get(uuid);
        if (pos == null || !pos.equals(blockPosition)) {
            return;
        }

        this.results = textLines;
    }
}
