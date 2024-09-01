package net.akazukin.library.compat.minecraft.data.packets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;

// ClientboundSetActionBarTextPacket
@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SSetActionBarTextPacket implements Packet {
    private final IChatBaseComponent text;
}
