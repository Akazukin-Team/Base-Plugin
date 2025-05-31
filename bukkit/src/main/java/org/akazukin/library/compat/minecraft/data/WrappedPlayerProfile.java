package org.akazukin.library.compat.minecraft.data;

import lombok.Data;

import java.util.UUID;

@Data
public class WrappedPlayerProfile {
    private UUID uniqueId;
    private String name;
    private String skin;
    private String skinModel;
    private String cape;
    private long timestamp;
}
