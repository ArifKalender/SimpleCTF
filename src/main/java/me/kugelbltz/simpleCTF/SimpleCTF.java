package me.kugelbltz.simpleCTF;

import me.kugelbltz.simpleCTF.commands.CaptureTheFlag;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.game.listeners.QueueListener;
import me.kugelbltz.simpleCTF.game.listeners.matchListeners.CombatListener;
import me.kugelbltz.simpleCTF.game.listeners.matchListeners.FlagInteractionListener;
import me.kugelbltz.simpleCTF.game.listeners.matchListeners.PlayerLifecycleListener;
import me.kugelbltz.simpleCTF.model.BannerItems;
import me.kugelbltz.simpleCTF.util.QueueHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class SimpleCTF extends JavaPlugin {

    private static BannerItems BANNER_ITEMS;
    private static MiniMessage MINI_MESSAGE;
    private static QueueHandler QUEUE_HANDLER;
    private static SimpleCTF plugin;
    private static Match currentMatch = null;

    public static SimpleCTF getInstance() {
        return plugin;
    }

    /**
     * @return The ongoing match, can return null if no match is going on.
     */
    public static Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Used only by {@link Match} to set the current match.
     *
     * @param match
     */
    public void setCurrentMatch(@Nullable Match match) {
        currentMatch = match;
    }

    public static BannerItems getBannerItems() {
        return BANNER_ITEMS;
    }

    /**
     * @return MiniMessage instance for the plugin
     */
    public static MiniMessage getMM() {
        return MINI_MESSAGE;
    }

    /**
     * @return The queue management class
     */
    public static QueueHandler getQueueHandler() {
        return QUEUE_HANDLER;
    }

    @Override
    public void onEnable() {
        plugin = this;
        initialize();
    }

    /**
     * General initialization of the plugin
     */
    private void initialize() {
        loadConfigs();
        registerListeners();
        registerCommands();
        MINI_MESSAGE = MiniMessage.miniMessage();
        BANNER_ITEMS = new BannerItems();
        QUEUE_HANDLER = new QueueHandler();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(), this);
        this.getServer().getPluginManager().registerEvents(new FlagInteractionListener(), this);
        this.getServer().getPluginManager().registerEvents(new CombatListener(), this);
        this.getServer().getPluginManager().registerEvents(new QueueListener(), this);
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("capturetheflag").setExecutor(new CaptureTheFlag());
    }

    /**
     * Saves the default config and caches the config
     */
    private void loadConfigs() {
        saveDefaultConfig();
        StaticVariables.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (getCurrentMatch() != null) getCurrentMatch().unloadMatch("<red>Server restart");
    }
}
