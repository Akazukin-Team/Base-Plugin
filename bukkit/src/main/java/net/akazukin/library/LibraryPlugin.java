package net.akazukin.library;

import io.netty.channel.Channel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import lombok.Getter;
import net.akazukin.library.command.LibraryBukkitCommandManager;
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
import net.akazukin.library.manager.BukkitMessageHelper;
import net.akazukin.library.packetlistener.BukkitChannelHandler;
import net.akazukin.library.packetlistener.InjectionManager;
import net.akazukin.library.packetlistener.client.SocketRemoteClient;
import net.akazukin.library.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LibraryPlugin extends JavaPlugin implements LibraryPluginAPI {
    public static I18nUtils I18N_UTILS;
    public static Compat COMPAT;
    public LibraryBukkitCommandManager commandManager;
    public LibraryEventManager eventManager;
    private ConfigUtils configUtils;
    private BukkitMessageHelper messageHelper;
    private InjectionManager injectionManager;

    {
        LibraryPluginProvider.setApi(this);
    }

    public static void main(final String[] args) {
    }

    @Override
    public void onLoad() {
        this.getLogManager().addHandler(new Handler() {
            private final File file = new File(LibraryPlugin.this.getDataFolder(), "error.log");

            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel() != Level.SEVERE && record.getThrown() == null) return;

                try (final FileWriter file = new FileWriter(this.file, true)) {
                    try (final BufferedWriter bw = new BufferedWriter(file)) {
                        try (final PrintWriter pw = new PrintWriter(bw)) {
                            pw.println("[" + record.getLevel() + "] " + record.getMessage());
                            if (record.getThrown() != null) {
                                record.getThrown().printStackTrace(pw);
                            }
                            pw.println("\n");
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });


        this.getLogManager().info("Initializing version manager...");
        COMPAT = CompatManager.initCompat(this);
        this.getLogManager().info("Successfully Initialized version manager");
    }

    @Override
    public Logger getLogManager() {
        return getPlugin().getLogger();
    }

    public static LibraryPlugin getPlugin() {
        return JavaPlugin.getPlugin(LibraryPlugin.class);
    }

    @Override
    public BukkitMessageHelper getMessageHelper() {
        return this.messageHelper;
    }

    @Override
    public void onDisable() {
        GuiManager.singleton().getScreens().keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(Player::closeInventory);

        final List<Channel> channels = LibraryPlugin.COMPAT.getServerChannels();
        if (channels == null) {
            this.getLogManager().warning("Couldn't get active server's channels !");
        } else {
            try {
                channels.stream()
                        .map(SocketRemoteClient::new)
                        .forEach(this.injectionManager::removeCustomHandler);
            } catch (final NoClassDefFoundError e) {
                this.getLogManager().warning("Couldn't remove packet listener from server's channels !");
            }
        }
    }

    @Override
    public void onEnable() {
        this.getLogManager().info("Initializing injection manager...");
        this.injectionManager = new InjectionManager(BukkitChannelHandler.class);
        this.getLogManager().info("Successfully injection manager");


        this.getLogManager().info("Initializing configurations...");
        this.configUtils = new ConfigUtils(this, "library");
        this.configUtils.loadConfigFiles("config.yaml");
        this.getLogManager().info("Successfully Initialized configurations");


        this.getLogManager().info("Initializing database...");
        LibrarySQLConfig.setFile(new File(this.getDataFolder(), "library.db"));
        final LibrarySQLConfig sqlCfg = LibrarySQLConfig.singleton();
        sqlCfg.getTransactionManager().required(() -> {
            new MUserDaoImpl(sqlCfg).create();
            new MUserProfileDaoImpl(sqlCfg).create();
        });
        this.getLogManager().info("Successfully Initialized database");


        this.getLogManager().info("Initializing I18n manager...");
        final YamlConfiguration config = this.configUtils.getConfig("config.yaml");
        I18N_UTILS = new I18nUtils(this, "library", this.getDataFolder());
        I18N_UTILS.build(config.getStringList("locales").toArray(new String[0]));
        this.messageHelper = new BukkitMessageHelper(I18N_UTILS);
        this.getLogManager().info("Successfully initialized I18n manager");


        this.getLogManager().info("Initializing event listeners...");
        this.eventManager = new LibraryEventManager();
        this.eventManager.registerListeners();
        Bukkit.getPluginManager().registerEvents(new Events(this.eventManager), this);
        this.getLogManager().info("Successfully initialized event listeners");


        this.getLogManager().info("Initializing command manager...");
        this.commandManager = new LibraryBukkitCommandManager(this);
        this.commandManager.registerCommands();
        this.getLogManager().info("Successfully Initialized command manager");


        this.getLogManager().info("Initializing event handler...");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                () -> Bukkit.getPluginManager().callEvent(new ServerTickEvent()), 0, 0);
        this.getLogManager().info("Successfully initialized event handler");


        this.getLogManager().info("Initializing packet handler...");
        final List<Channel> channels = COMPAT.getServerChannels();
        if (channels == null) {
            this.getLogManager().warning("Couldn't get active server's channels !");
            this.getLogManager().info("Failed to initialize packet listener");
        } else {
            channels.stream()
                    .map(SocketRemoteClient::new)
                    .forEach(this.injectionManager::injectCustomHandler);
            this.getLogManager().info("Successfully initialized packet handler");
        }


        this.getLogManager().info("Successfully enabled");
    }
}
