package net.akazukin.library;

import io.netty.channel.Channel;
import net.akazukin.library.compat.minecraft.Compat;
import net.akazukin.library.compat.minecraft.CompatManager;
import net.akazukin.library.doma.LibrarySQLConfig;
import net.akazukin.library.doma.dao.MUserDaoImpl;
import net.akazukin.library.event.Events;
import net.akazukin.library.event.LibraryEventManager;
import net.akazukin.library.gui.GuiManager;
import net.akazukin.library.i18n.I18nUtils;
import net.akazukin.library.managers.TaskManager;
import net.akazukin.library.packetlistener.InjectionUtils;
import net.akazukin.library.utils.AuthUtils;
import net.akazukin.library.utils.ConfigUtils;
import net.akazukin.library.utils.MessageHelper;
import net.akazukin.library.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

public final class LibraryPlugin extends JavaPlugin {

    public static String PLUGIN_NAME;
    public static ConfigUtils CONFIG_UTILS;
    public static I18nUtils I18N_UTILS;
    public static LibraryEventManager EVENT_MANAGER;
    public static Compat COMPAT;
    public static MessageHelper MESSAGE_HELPER;

    public static LibraryPlugin getPlugin() {
        return getPlugin(LibraryPlugin.class);
    }

    public static Logger getLogManager() {
        return getPlugin().getLogger();
    }

    @Override
    public void onEnable() {
        LibraryPlugin.PLUGIN_NAME = getName();
        

        getLogManager().info("Initializing version manager...");
        COMPAT = CompatManager.initCompat();
        getLogManager().info("Successfully Initialized version manager");


        getLogManager().info("Initializing database...");
        LibrarySQLConfig.setFile(new File(getDataFolder(), "library.db"));
        final LibrarySQLConfig sqlCfg = LibrarySQLConfig.singleton();
        sqlCfg.getTransactionManager().required(() -> {
            new MUserDaoImpl(sqlCfg).create();
        });
        getLogManager().info("Successfully Initialized database");


        getLogManager().info("Initializing packet listener...");
        final List<Channel> channels = COMPAT.getServerChannels();
        if (channels == null) {
            getLogManager().warning("Couldn't get active server's channels !");
            getLogManager().info("Failed to initialize packet listener");
            return;
        } else {
            channels.forEach((ch) -> {
                final Player player = PlayerUtils.getPlayerFromAddress((InetSocketAddress) ch.remoteAddress());
                if (player != null)
                    InjectionUtils.injectCustomHandler(player, ch);
            });
        }
        getLogManager().info("Successfully initialized packet listener");


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


        getLogManager().info("Starting synchronized threader...");
        new Thread(TaskManager::runLoop).start();
        getLogManager().info("Successfully Started synchronized threader");


        Bukkit.broadcastMessage("Successfully enabled");
    }

    @Override
    public void onDisable() {
        GuiManager.singleton().getScreens().keySet().forEach(player -> Bukkit.getPlayer(player).closeInventory());

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
    public void onLoad() {
        CONFIG_UTILS = new ConfigUtils(this, "library");
        CONFIG_UTILS.loadConfigFiles("config.yaml");
        final YamlConfiguration config = CONFIG_UTILS.getConfig("config.yaml");

        getLogManager().info("Authenticating in Akazukin-Team Database...");
        if (!config.contains("token") || !AuthUtils.auth("AkazukinLibraryPlugin", config.getString("token"))) {
            getLogManager().severe("Failed to Authenticate!");
            setEnabled(false);
            return;
        }
        getLogManager().info("Successfully authenticated!");
    }
}
