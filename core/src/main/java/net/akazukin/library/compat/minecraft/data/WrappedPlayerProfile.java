package net.akazukin.library.compat.minecraft.data;

import java.util.UUID;
import lombok.Data;

@Data
public class WrappedPlayerProfile {
    private UUID uniqueId;
    private String name;
    private String skin;
    private String skinModel;
    private String cape;
    private long timestamp;
}
