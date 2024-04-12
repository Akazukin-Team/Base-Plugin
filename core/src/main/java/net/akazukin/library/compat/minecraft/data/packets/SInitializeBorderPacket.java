package net.akazukin.library.compat.minecraft.data.packets;


import lombok.Data;
import org.bukkit.WorldBorder;

@Data
public class SInitializeBorderPacket {
    WorldBorder worldBorder;
}
