package org.akazukin.library.compat.minecraft.data.packets;

import com.mojang.datafixers.util.Pair;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// PacketPlayOutEntityEquipment
@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SEntityEquipmentPacket implements Packet {
    int stateId;
    List<Pair<Integer, ItemStack>> slots;
}
