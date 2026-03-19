package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MatchMapConfig {

    private File file = new File(SimpleCTF.getInstance().getDataFolder(), "MatchMaps.yml");
    private YamlConfiguration cachedConfig;
    public MatchMapConfig() {
        if (!file.exists()) {
            SimpleCTF.getInstance().saveResource("MatchMaps.yml", false);
        }
    }
    public FileConfiguration getConfig() {
        if (cachedConfig == null) return reloadConfig();
        else return cachedConfig;
    }


    public YamlConfiguration reloadConfig() {
        this.cachedConfig = YamlConfiguration.loadConfiguration(file);
        return this.cachedConfig;
    }
}
