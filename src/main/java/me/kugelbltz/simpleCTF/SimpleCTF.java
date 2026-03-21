package me.kugelbltz.simpleCTF;

import me.kugelbltz.simpleCTF.commands.CaptureTheFlag;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.game.listeners.MatchListener;
import me.kugelbltz.simpleCTF.model.BannerItems;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class SimpleCTF extends JavaPlugin {

    private static SimpleCTF plugin;
    private static ConfigManager configManager;
    private static Match currentMatch = null;
    public static BannerItems BANNER_ITEMS;
    public static MiniMessage MM;

    @Override
    public void onEnable() {
        plugin = this;
        initialize();
    }

    public static SimpleCTF getInstance() {
        return plugin;
    }
    private void initialize() {
        MM = MiniMessage.miniMessage();
        loadConfigs();
        registerListeners();
        registerCommands();
        BANNER_ITEMS = new BannerItems();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MatchListener(), this);
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("capturetheflag").setExecutor(new CaptureTheFlag());
    }

    private void loadConfigs() {
        saveDefaultConfig();
        configManager = new ConfigManager();
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
    }

}
