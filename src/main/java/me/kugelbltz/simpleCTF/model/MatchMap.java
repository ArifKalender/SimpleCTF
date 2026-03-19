package me.kugelbltz.simpleCTF.model;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class MatchMap {

    private Location blueFlag, redFlag, blueSpawn, redSpawn;
    private String worldName;
    private World world;
    private static final HashMap<String, MatchMap> registeredWorlds = new HashMap<>();

    public MatchMap(String worldName, Location blueFlag, Location redFlag, Location blueSpawn, Location redSpawn) {
        this.worldName = worldName;
        this.blueFlag = blueFlag;
        this.redFlag = redFlag;
        this.blueSpawn = blueSpawn;
        this.redSpawn = redSpawn;
        registeredWorlds.put(this.worldName, this);
    }

    public void loadWorldForPlayers(List<Player> bluePlayers, List<Player> redPlayers) {
        // TODO: optimized world loading
        loadWorld();
        bluePlayers.forEach(player -> {
            player.teleportAsync(this.blueSpawn);
        });
        redPlayers.forEach(player -> {
            player.teleportAsync(this.redSpawn);
        });
    }
    private void loadWorld() {
        WorldCreator creator = new WorldCreator(this.worldName);
        creator.generateStructures(false);
        this.world = Bukkit.createWorld(creator);
        this.world.setAutoSave(false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setClearWeatherDuration(1000000000);
        world.setTime(6000);
    }
    public boolean isOccupied() {
        return getBukkitWorld() != null;
    }
    public World getBukkitWorld() {
        return world;
    }

    public static MatchMap getMatchMap(String worldName) {
        return registeredWorlds.get(worldName);
    }
}
