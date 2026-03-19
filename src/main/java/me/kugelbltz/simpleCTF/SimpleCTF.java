package me.kugelbltz.simpleCTF;

import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.configuration.MatchMapConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCTF extends JavaPlugin {

    private static SimpleCTF plugin;
    private static ConfigManager configManager;
    private static MatchMapConfig matchMapConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        initialize();
    }

    public static SimpleCTF getInstance() {
        return plugin;
    }
    private void initialize() {
        loadConfigs();
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {

    }

    private void registerCommands() {

    }

    private void loadConfigs() {
        matchMapConfig = new MatchMapConfig();
        configManager = new ConfigManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public MatchMapConfig getMatchMapConfig() {
        return matchMapConfig;
    }

}
