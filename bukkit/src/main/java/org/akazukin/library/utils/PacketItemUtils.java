package org.akazukin.library.utils;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class PacketItemUtils {
    @Nullable
    public String getDisplayName(@NotNull final ItemStack item) {
        final NBTString nbt = getNBT(item, new String[]{"display", "Name"}, NBTString.class);
        if (nbt == null) {
            return null;
        }

        return LegacyComponentSerializer.legacySection().serialize(GsonComponentSerializer.gson().deserialize(nbt.getValue()));
    }

    @Nullable
    public <T extends NBT> T getNBT(@NotNull final ItemStack item, final String[] keys, final Class<T> clazz) {
        NBTCompound nbt = item.getNBT();
        if (nbt == null) {
            return null;
        }

        for (int i = 0; i < keys.length - 1; i++) {
            nbt = nbt.getCompoundTagOrNull(keys[i]);
            if (nbt == null) {
                return null;
            }
        }
        return nbt.getTagOfTypeOrNull(keys[keys.length - 1], clazz);
    }

    public ItemStack.Builder copy(@NotNull final ItemStack item) {
        return ItemStack.builder()
                .type(item.getType())
                .amount(item.getAmount())
                .nbt(item.getNBT())
                .components(item.getComponents())
                .legacyData(item.getLegacyData())
                .version(item.getVersion());
    }

    public void setDisplayName(final ItemStack item, final Component displayName) {
        setNBT(item,
                new String[]{"display", "Name"},
                new NBTString(GsonComponentSerializer.gson().serialize(displayName)));
    }

    public void setNBT(@NotNull final ItemStack item, final String[] keys, final NBT value) {
        NBTCompound nbt = item.getNBT();
        if (nbt == null) {
            nbt = new NBTCompound();
            item.setNBT(nbt);
        }

        for (int i = 0; i < keys.length - 1; i++) {
            NBTCompound nbt2 = nbt.getCompoundTagOrNull(keys[i]);
            if (nbt2 == null) {
                nbt2 = new NBTCompound();
                nbt.setTag(keys[1], nbt);
            }
            nbt = nbt2;
        }
        nbt.setTag(keys[keys.length - 1], value);
    }

    public void setCustomData(final ItemStack item, final String[] keys, final NBT value) {
        NBTCompound nbt;
        {
            final Optional<NBTCompound> compOpt = item.getComponent(ComponentTypes.CUSTOM_DATA);
            if (compOpt.isPresent()) {
                nbt = compOpt.get();
            } else {
                nbt = new NBTCompound();
                item.setComponent(ComponentTypes.CUSTOM_DATA, Optional.of(nbt));
            }
        }

        for (int i = 0; i < keys.length - 1; i++) {
            NBTCompound nbt2 = nbt.getCompoundTagOrNull(keys[i]);
            if (nbt2 == null) {
                nbt2 = new NBTCompound();
                nbt.setTag(keys[1], nbt);
            }
            nbt = nbt2;
        }
        nbt.setTag(keys[keys.length - 1], value);
    }

    public void setLore(final ItemStack item, final Component[] lores) {
        final NBTList<NBTString> loreList = new NBTList<>(NBTType.STRING);
        for (final Component lore : lores) {
            loreList.addTag(new NBTString(GsonComponentSerializer.gson().serialize(lore)));
        }
        setNBT(item, new String[]{"display", "Lore"}, loreList);
    }

    public <T extends NBT> T getCustomData(@NotNull final ItemStack item, final String[] keys, final Class<T> clazz) {
        NBTCompound nbt;
        {
            final Optional<NBTCompound> compOpt = item.getComponent(ComponentTypes.CUSTOM_DATA);
            if (compOpt.isPresent()) {
                nbt = compOpt.get();
            } else {
                return null;
            }
        }

        for (int i = 0; i < keys.length - 1; i++) {
            nbt = nbt.getCompoundTagOrNull(keys[i]);
            if (nbt == null) {
                return null;
            }
        }
        return nbt.getTagOfTypeOrNull(keys[keys.length - 1], clazz);
    }

    public void removeCustomData(@NotNull final ItemStack item, final String[] keys) {
        final List<NBTCompound> nbts = new ArrayList<>();

        NBTCompound nbt;
        {
            final Optional<NBTCompound> compOpt = item.getComponent(ComponentTypes.CUSTOM_DATA);
            if (compOpt.isPresent()) {
                nbt = compOpt.get();
            } else {
                return;
            }
        }

        nbts.add(nbt);

        for (int i = 0; i < keys.length - 1; i++) {
            nbt = nbt.getCompoundTagOrNull(keys[i]);
            if (nbt == null) {
                return;
            }
            nbts.add(nbt);
        }

        nbt.removeTag(keys[keys.length - 1]);

        while (!nbts.isEmpty()) {
            nbts.remove(nbts.size() - 1).removeTag(keys[keys.length - 1]);
        }
    }


    public @Nullable UUID getPlayer(@NotNull final ItemStack item) {
        final NBTString nbtString = getNBT(item, new String[]{"SkullOwner", "Id"}, NBTString.class);
        if (nbtString == null) {
            return null;
        }
        return UUID.fromString(nbtString.getValue());
    }

    public ItemStack getSkullItem(@NotNull final UUID player) {
        final ItemStack item = ItemStack.builder()
                .type(ItemTypes.PLAYER_HEAD)
                .amount(1)
                .build();

        final WrappedPlayerProfile profile = PlayerUtils.get(player);

        setNBT(item, new String[]{"SkullOwner", "Id"}, new NBTString(profile.getUniqueId().toString()));
        setNBT(item, new String[]{"SkullOwner", "Name"}, new NBTString(profile.getName()));

// テクスチャプロパティの構築
        final NBTCompound texture = new NBTCompound();
        texture.setTag("Value", new NBTString(profile.getSkin()));

        final NBTList<NBTCompound> texturesList = new NBTList<>(NBTType.COMPOUND);
        texturesList.addTag(texture);

        final NBTCompound properties = new NBTCompound();
        properties.setTag("textures", texturesList);

        setNBT(item, new String[]{"SkullOwner", "Properties"}, properties);

        return item;
    }
}
