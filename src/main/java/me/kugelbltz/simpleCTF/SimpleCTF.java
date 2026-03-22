package me.kugelbltz.simpleCTF;

import me.kugelbltz.simpleCTF.commands.CaptureTheFlag;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.game.listeners.MatchListener;
import me.kugelbltz.simpleCTF.game.listeners.QueueListener;
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

    @Override
    public void onEnable() {
        plugin = this;
        initialize();
    }

    private void initialize() {
        loadConfigs();
        registerListeners();
        registerCommands();
        MINI_MESSAGE = MiniMessage.miniMessage();
        BANNER_ITEMS = new BannerItems();
        QUEUE_HANDLER = new QueueHandler();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MatchListener(), this);
        this.getServer().getPluginManager().registerEvents(new QueueListener(), this);
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("capturetheflag").setExecutor(new CaptureTheFlag());
    }

    private void loadConfigs() {
        saveDefaultConfig();
        StaticVariables.init();
    }

    public Match getCurrentMatch() {
        return currentMatch;
    }

    public void setCurrentMatch(@Nullable Match match) {
        currentMatch = match;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (getCurrentMatch() != null) getCurrentMatch().unloadMatch("<red>Server restart");
    }

    public static BannerItems getBannerItems() {
        return BANNER_ITEMS;
    }

    public static MiniMessage getMM() {
        return MINI_MESSAGE;
    }

    public static QueueHandler getQueueHandler() {
        return QUEUE_HANDLER;
    }
}
