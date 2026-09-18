package org.akazukin.library.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserProfileEntity;
import org.akazukin.library.doma.repo.MUserProfileRepo;
import org.akazukin.util.utils.http.HttpUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class PlayerUtils {
    public static GameProfile getProfile(final WrappedPlayerProfile profile) {
        final GameProfile profile2 = new GameProfile(profile.getUniqueId(), profile.getName());
        final JsonObject json = new JsonObject();
        final JsonObject texture = new JsonObject();
        final JsonObject skin = new JsonObject();
        final JsonObject cape = new JsonObject();
        json.addProperty("timestamp", profile.getTimestamp());
        json.addProperty("profileName", profile.getName());
        if (profile.getSkin() != null) {
            skin.addProperty("url", profile.getSkin());
        }
        if (profile.getSkinModel() != null) {
            final JsonObject skinMeta = new JsonObject();
            skinMeta.addProperty("model", profile.getSkinModel());
            skin.add("metadata", skinMeta);
        }
        if (!skin.isEmpty()) {
            texture.add("SKIN", skin);
        }
        if (profile.getSkinModel() != null) {
            cape.addProperty("url", profile.getCape());
            texture.add("CAPE", cape);
        }
        json.add("textures", texture);
        profile2.getProperties().put("textures", new Property("textures",
                new String(EncodeUtils.encodeBase64(json.toString().getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8), null));
        return profile2;
    }

    public static WrappedPlayerProfile get(final UUID player) {
        WrappedPlayerProfile profile = PlayerUtils.load(player);
        if (profile != null) {
            return profile;
        }
        profile = PlayerUtils.fetchProfile(player);
        if (profile != null) {
            PlayerUtils.save(profile);
        }
        return profile;
    }

    @Nullable
    public static WrappedPlayerProfile load(@NotNull final UUID player) {
        final MUserProfileEntity entity = LibrarySQLConfig.singleton().getTransactionManager().required(() ->
                MUserProfileRepo.selectById(player));
        if (entity == null) {
            return null;
        }

        final WrappedPlayerProfile profile = new WrappedPlayerProfile();

        profile.setUniqueId(entity.getPlayerUuid());
        profile.setName(entity.getName());
        profile.setSkin(entity.getSkin());
        profile.setSkinModel(entity.getSkinModel());
        profile.setCape(entity.getCape());
        profile.setTimestamp(entity.getTimestamp());

        return profile;
    }

    public static WrappedPlayerProfile fetchProfile(final UUID player) {
        final byte[] res;
        try {
            res = HttpUtils.request("https://sessionserver.mojang.com/session/minecraft/profile/" + player, null, null, "GET").getResponse();
        } catch (final IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
            //throw new RuntimeException(e);
        }
        if (res == null) {
            return null;
        }

        final JsonObject data = JsonParser.parseString(new String(EncodeUtils.decodeBase64(
                        JsonParser.parseString(new String(
                                        res,
                                        StandardCharsets.UTF_8))
                                .getAsJsonObject()
                                .getAsJsonArray("properties")
                                .get(0)
                                .getAsJsonObject()
                                .get("value")
                                .getAsString()), StandardCharsets.UTF_8)
                )
                .getAsJsonObject();

        final WrappedPlayerProfile profile = new WrappedPlayerProfile();

        profile.setUniqueId(player);
        profile.setName(data.get("profileName").getAsString());
        profile.setSkin(data
                .getAsJsonObject("textures")
                .getAsJsonObject("SKIN")
                .get("url")
                .getAsString()
        );
        if (data.getAsJsonObject("textures").getAsJsonObject("SKIN").has("metadata")) {
            profile.setSkinModel(data
                    .getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .getAsJsonObject("metadata")
                    .get("model")
                    .getAsString()
                    .toUpperCase()
            );
        }
        if (data.getAsJsonObject("textures").has("CAPE")) {
            profile.setCape(data
                    .getAsJsonObject("textures")
                    .getAsJsonObject("CAPE")
                    .get("url")
                    .getAsString()
            );
        }
        profile.setTimestamp(data.get("timestamp").getAsLong());

        return profile;
    }

    public static void save(final WrappedPlayerProfile profile) {
        LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
            MUserProfileEntity entity = MUserProfileRepo.selectById(profile.getUniqueId());
            if (entity == null) {
                entity = new MUserProfileEntity();
                entity.setPlayerUuid(profile.getUniqueId());
            }
            entity.setName(profile.getName());
            entity.setSkin(profile.getSkin());
            entity.setSkinModel(profile.getSkinModel());
            entity.setCape(profile.getCape());
            entity.setTimestamp(profile.getTimestamp());
            MUserProfileRepo.save(entity);
        });
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull final String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> name.equalsIgnoreCase(p.getName())).findFirst().orElse(null);
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull final UUID uuid) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> uuid.equals(p.getUniqueId())).findFirst().orElse(null);
    }
}
