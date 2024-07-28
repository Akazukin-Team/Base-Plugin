package net.akazukin.library;

import io.netty.channel.Channel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.akazukin.library.command.LibraryCommandManager;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.CompatManager;
import net.akazukin.library.doma.LibrarySQLConfig;
import net.akazukin.library.doma.dao.MUserDaoImpl;
import net.akazukin.library.doma.dao.MUserProfileDaoImpl;
import net.akazukin.library.event.Events;
import net.akazukin.library.event.LibraryEventManager;
import net.akazukin.library.event.events.ServerTickEvent;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.i18n.I18nUtils;
import net.akazukin.library.packetlistener.InjectionUtils;
import net.akazukin.library.utils.ConfigUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class LibraryPlugin extends JavaPlugin {
    public static LibraryCommandManager COMMAND_MANAGER;
    public static ConfigUtils CONFIG_UTILS;
    public static I18nUtils I18N_UTILS;
    public static LibraryEventManager EVENT_MANAGER;
    public static Compat COMPAT;
    public static MessageHelper MESSAGE_HELPER;

    public static void main(final String[] args) {
    }

    @Override
    public void onLoad() {
        getPlugin().getLogger().addHandler(new Handler() {
            private final File file = new File(LibraryPlugin.getPlugin().getDataFolder(), "error.log");

            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel() == Level.SEVERE || record.getThrown() != null) {
                    try (final FileWriter file = new FileWriter(this.file, true)) {
                        try (final PrintWriter pw = new PrintWriter(new BufferedWriter(file))) {
                            pw.println("[" + record.getLevel() + "] " + record.getMessage());
                            //pw.println(pw);
                            if (record.getThrown() != null) {
                                record.getThrown().printStackTrace(pw);
                            }
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });


        getLogManager().info("Initializing version manager...");
        COMPAT = CompatManager.initCompat(this);
        getLogManager().info("Successfully Initialized version manager");
    }

    public static LibraryPlugin getPlugin() {
        return JavaPlugin.getPlugin(LibraryPlugin.class);
    }

    public static Logger getLogManager() {
        return getPlugin().getLogger();
    }

    @Override
    public void onDisable() {
        GuiManager.singleton().getScreens().keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(Player::closeInventory);

        final List<Channel> channels = LibraryPlugin.COMPAT.getServerChannels();
        if (channels == null) {
            getLogManager().warning("Couldn't get active server's channels !");
        } else {
            try {
                channels.forEach(InjectionUtils::removeCustomHandler);
            } catch (final NoClassDefFoundError e) {
                getLogManager().warning("Couldn't remove packet listener from server's channels !");
            }
        }
    }

    @Override
    public void onEnable() {
        getLogManager().info("Initializing configurations...");
        CONFIG_UTILS = new ConfigUtils(this, "library");
        CONFIG_UTILS.loadConfigFiles("config.yaml");
        getLogManager().info("Successfully Initialized configurations");


        getLogManager().info("Initializing database...");
        LibrarySQLConfig.setFile(new File(this.getDataFolder(), "library.db"));
        final LibrarySQLConfig sqlCfg = LibrarySQLConfig.singleton();
        sqlCfg.getTransactionManager().required(() -> {
            new MUserDaoImpl(sqlCfg).create();
            new MUserProfileDaoImpl(sqlCfg).create();
        });
        getLogManager().info("Successfully Initialized database");


        getLogManager().info("Initializing I18n manager...");
        final YamlConfiguration config = CONFIG_UTILS.getConfig("config.yaml");
        I18N_UTILS = new I18nUtils(this, "library");
        I18N_UTILS.build(config.getList("locales").toArray(new String[0]));
        MESSAGE_HELPER = new MessageHelper(I18N_UTILS);
        getLogManager().info("Successfully initialized I18n manager");


        getLogManager().info("Initializing event listeners...");
        EVENT_MANAGER = new LibraryEventManager();
        EVENT_MANAGER.registerListeners();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getLogManager().info("Successfully initialized event listeners");


        getLogManager().info("Initializing command manager...");
        COMMAND_MANAGER = new LibraryCommandManager(this);
        COMMAND_MANAGER.registerCommands();
        getLogManager().info("Successfully Initialized command manager");


        getLogManager().info("Initializing event handler...");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                () -> Bukkit.getPluginManager().callEvent(new ServerTickEvent()), 0, 0);
        getLogManager().info("Successfully initialized event handler");


        getLogManager().info("Initializing packet handler...");
        final List<Channel> channels = COMPAT.getServerChannels();
        if (channels == null) {
            getLogManager().warning("Couldn't get active server's channels !");
            getLogManager().info("Failed to initialize packet listener");
        } else {
            channels.forEach((ch) -> {
                final Player player = PlayerUtils.getPlayerFromAddress((InetSocketAddress) ch.remoteAddress());
                if (player != null)
                    InjectionUtils.injectCustomHandler(player, ch);
            });
        }
        getLogManager().info("Successfully initialized packet handler");


        getLogManager().info("Successfully enabled");
    }
}
