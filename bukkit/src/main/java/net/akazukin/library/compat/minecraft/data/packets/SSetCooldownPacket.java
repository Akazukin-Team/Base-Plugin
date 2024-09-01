package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

// PacketPlayOutSetCooldown
@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SSetCooldownPacket implements Packet {
    ItemStack item;
    int cooltime;
}
