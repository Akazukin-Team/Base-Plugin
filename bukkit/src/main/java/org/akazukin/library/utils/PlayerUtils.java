package org.akazukin.library.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.akazukin.library.compat.minecraft.data.WrappedPlayerProfile;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserProfileEntity;
import org.akazukin.library.doma.repo.MUserProfileRepo;
import org.akazukin.util.utils.http.HttpMethod;
import org.akazukin.util.utils.http.HttpUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerUtils {
    public static Player getPlayerFromAddress(final InetSocketAddress addr) {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getAddress().equals(addr)) return player;
        }
        return null;
    }

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
        if (skin.size() > 0)
            texture.add("SKIN", skin);
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
        if (profile == null) {
            profile = PlayerUtils.fetchProfile(player);
            if (profile != null)
                PlayerUtils.save(profile);
        }

        return profile;
    }

    public static WrappedPlayerProfile fetchProfile(final UUID player) {
        final byte[] res;
        try {
            res = HttpUtils.request("https://sessionserver.mojang.com/session/minecraft/profile/" + player, HttpMethod.GET);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
            //throw new RuntimeException(e);
        }
        if (res == null) return null;

        final JsonParser parser = new JsonParser();
        final JsonObject data = parser.parse(new String(EncodeUtils.decodeBase64(parser
                        .parse(new String(
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
        if (data.getAsJsonObject("textures").getAsJsonObject("SKIN").has("metadata"))
            profile.setSkinModel(data
                    .getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .getAsJsonObject("metadata")
                    .get("model")
                    .getAsString()
                    .toUpperCase()
            );
        if (data.getAsJsonObject("textures").has("CAPE"))
            profile.setCape(data
                    .getAsJsonObject("textures")
                    .getAsJsonObject("CAPE")
                    .get("url")
                    .getAsString()
            );
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

    public static WrappedPlayerProfile load(final UUID player) {
        final MUserProfileEntity entity = LibrarySQLConfig.singleton().getTransactionManager().required(() ->
                MUserProfileRepo.selectById(player)
        );
        if (entity == null) return null;
        final WrappedPlayerProfile profile = new WrappedPlayerProfile();

        profile.setUniqueId(entity.getPlayerUuid());
        profile.setName(entity.getName());
        profile.setSkin(entity.getSkin());
        profile.setSkinModel(entity.getSkinModel());
        profile.setCape(entity.getCape());
        profile.setTimestamp(entity.getTimestamp());

        return profile;
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@Nonnull String name) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> name.equalsIgnoreCase(p.getName())).findFirst().orElse(null);
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@Nonnull UUID uuid) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> uuid.equals(p.getUniqueId())).findFirst().orElse(null);
    }
}
