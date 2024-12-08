package org.akazukin.library.compat.minecraft.data.packets;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.WorldBorder;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CInitializeBorderPacket {
    WorldBorder worldBorder;
}
