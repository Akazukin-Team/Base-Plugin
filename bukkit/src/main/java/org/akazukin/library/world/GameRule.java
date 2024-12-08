package org.akazukin.library.world;

import lombok.Getter;

public class GameRule<T> {
    public static final GameRule<Boolean> ANNOUNCE_ADVANCEMENTS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> COMMAND_BLOCK_OUTPUT = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DISABLE_ELYTRA_MOVEMENT_CHECK = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_DAYLIGHT_CYCLE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_ENTITY_DROPS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_FIRE_TICK = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_LIMITED_CRAFTING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_MOB_LOOT = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_MOB_SPAWNING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_TILE_DROPS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_WEATHER_CYCLE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> KEEP_INVENTORY = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> LOG_ADMIN_COMMANDS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> MOB_GRIEFING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> NATURAL_REGENERATION = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> REDUCED_DEBUG_INFO = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> SEND_COMMAND_FEEDBACK = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> SHOW_DEATH_MESSAGES = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> SPECTATORS_GENERATE_CHUNKS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DISABLE_RAIDS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_INSOMNIA = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_IMMEDIATE_RESPAWN = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DROWNING_DAMAGE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> FALL_DAMAGE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> FIRE_DAMAGE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> FREEZE_DAMAGE = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_PATROL_SPAWNING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_TRADER_SPAWNING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_WARDEN_SPAWNING = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> FORGIVE_DEAD_PLAYERS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> UNIVERSAL_ANGER = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> BLOCK_EXPLOSION_DROP_DECAY = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> MOB_EXPLOSION_DROP_DECAY = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> TNT_EXPLOSION_DROP_DECAY = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> WATER_SOURCE_CONVERSION = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> LAVA_SOURCE_CONVERSION = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> GLOBAL_SOUND_EVENTS = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> DO_VINES_SPREAD = new GameRule<>(Boolean.class);
    public static final GameRule<Boolean> ENDER_PEARLS_VANISH_ON_DEATH = new GameRule<>(Boolean.class);
    public static final GameRule<Integer> RANDOM_TICK_SPEED = new GameRule<>(Integer.class);
    public static final GameRule<Integer> SPAWN_RADIUS = new GameRule<>(Integer.class);
    public static final GameRule<Integer> MAX_ENTITY_CRAMMING = new GameRule<>(Integer.class);
    public static final GameRule<Integer> MAX_COMMAND_CHAIN_LENGTH = new GameRule<>(Integer.class);
    public static final GameRule<Integer> COMMAND_MODIFICATION_BLOCK_LIMIT = new GameRule<>(Integer.class);
    public static final GameRule<Integer> PLAYERS_SLEEPING_PERCENTAGE = new GameRule<>(Integer.class);
    public static final GameRule<Integer> SNOW_ACCUMULATION_HEIGHT = new GameRule<>(Integer.class);

    @Getter
    private final Class<T> type;

    public GameRule(final Class<T> type) {
        this.type = type;
    }
}
