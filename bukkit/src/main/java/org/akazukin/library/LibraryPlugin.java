package org.akazukin.library;

import com.github.retrooper.packetevents.PacketEvents;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.config.I18nManagerConfig;
import org.akazukin.i18n.config.II18nManagerConfig;
import org.akazukin.i18n.manager.I18nManager;
import org.akazukin.i18n.manager.II18nManager;
import org.akazukin.library.command.LibraryBukkitCommandManager;
import org.akazukin.library.compat.minecraft.CompatManager;
import org.akazukin.library.compat.minecraft.ICompat;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.dao.MUserDaoImpl;
import org.akazukin.library.doma.dao.MUserProfileDaoImpl;
import org.akazukin.library.event.EventListeners;
import org.akazukin.library.event.LibraryEventManager;
import org.akazukin.library.event.PacketEventListeners;
import org.akazukin.library.event.events.ServerTickEvent;
import org.akazukin.library.gui.GuiManager;
import org.akazukin.library.manager.BukkitMessageHelper;
import org.akazukin.library.manager.I18nLocaleManager;
import org.akazukin.library.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class LibraryPlugin extends JavaPlugin implements LibraryPluginAPI {
    II18nManager i18nManager;
    LibraryBukkitCommandManager commandManager;
    LibraryEventManager eventManager;
    GuiManager guiManager;
    ICompat compat;
    ConfigUtils configUtils;
    BukkitMessageHelper messageHelper;
    I18nLocaleManager i18nLocaleMgr;
    GuiManager guiMgr;
    private PacketEventListeners packetEventListener;

    {
        LibraryPluginProvider.setApi(this);
    }

    @Override
    public void onLoad() {
        this.getLogManager().addHandler(new Handler() {
            private final File file = new File(LibraryPlugin.this.getDataFolder(), "error.log");

            @Override
            public void publish(final LogRecord record) {
                if (record.getLevel() != Level.SEVERE && record.getThrown() == null) {
                    return;
                }

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

        this.guiMgr = new GuiManager(this);

        this.getLogManager().info("Initializing version manager...");
        this.compat = CompatManager.initCompat(this);
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
        this.guiManager.getScreens().keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(Player::closeInventory);

        PacketEvents.getAPI().getEventManager().unregisterListener(this.packetEventListener);
        this.packetEventListener = null;
    }

    @Override
    public void onEnable() {
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

        {
            final II18nManagerConfig i18nCfg = I18nManagerConfig.builder()
                    .setClassLoader(this.getClassLoader())
                    .setDomain("org.akazukin")
                    .setAppId("library")
                    .setDataFolder(this.getDataFolder())
                    .build();
            this.i18nManager = I18nManager.create(i18nCfg);
        }
        this.messageHelper = new BukkitMessageHelper(this.i18nManager.getFormatter());
        this.getLogManager().info("Successfully initialized I18n manager");


        this.getLogManager().info("Initializing event listeners...");
        this.eventManager = new LibraryEventManager(this);
        this.eventManager.registerListeners();
        Bukkit.getPluginManager().registerEvents(new EventListeners(this.eventManager), this);
        this.getLogManager().info("Successfully initialized event listeners");


        this.getLogManager().info("Initializing command manager...");
        this.commandManager = new LibraryBukkitCommandManager(this);
        this.commandManager.registerCommands();
        this.getLogManager().info("Successfully Initialized command manager");


        this.getLogManager().info("Initializing event handler...");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                () -> Bukkit.getPluginManager().callEvent(new ServerTickEvent()), 0, 0);
        this.getLogManager().info("Successfully initialized event handler");


        {
            this.packetEventListener = new PacketEventListeners(this.eventManager);
            PacketEvents.getAPI().getEventManager().registerListener(this.packetEventListener);
        }


        this.getLogManager().info("Successfully enabled");
    }
}
