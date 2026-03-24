package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaticVariables {

    private static int MAX_PLAYERS_PER_TEAM;
    private static int MIN_PLAYERS_PER_TEAM;
    private static int MATCH_TIME;
    private static int WIN_SCORE;
    private static int FLAG_BASE_RADIUS;
    private static boolean RESET_MATCH_AFTER_SCORE;
    private static boolean STARTER_KIT_ENABLED;
    private static List<ItemStack> PLAYER_KIT;
    private static Location SPAWN;

    public static void init() {
        MAX_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MaxPlayersPerTeam", 4);
        MIN_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MinPlayersPerTeam", 1);
        MATCH_TIME = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.MatchTime", 600);
        WIN_SCORE = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.WinScore", 3);
        FLAG_BASE_RADIUS = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.FlagBaseRadius", 4);
        RESET_MATCH_AFTER_SCORE = SimpleCTF.getInstance().getConfig().getBoolean("SimpleCTF.Game.Match.ResetMatchAfterScore", true);
        STARTER_KIT_ENABLED = SimpleCTF.getInstance().getConfig().getBoolean("SimpleCTF.Game.Match.StarterKit.Enabled", true);
        SPAWN = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.Spawn", Bukkit.getWorlds().getFirst().getSpawnLocation());
        loadPlayerKit();
    }

    private static void loadPlayerKit() {
        List<String> loopItems = SimpleCTF.getInstance().getConfig().getStringList("SimpleCTF.Game.Match.StarterKit.Items");
        PLAYER_KIT = new ArrayList<>();
        for (String loopItem : loopItems) {
            String[] configValue = loopItem.split(":");
            String tryItem = configValue[0];
            int count = 1;
            if (configValue.length == 2) count = Integer.parseInt(configValue[1]);
            try {
                Material material = Material.valueOf(tryItem);
                ItemStack toAdd = new ItemStack(material, count);
                PLAYER_KIT.add(toAdd);
            } catch (IllegalArgumentException ignored) {
                SimpleCTF.getInstance().getLogger().warning("Item " + tryItem + " in SimpleCTF.Game.Match.StarterKit.Items was not recognized as a valid Material.");
            }
        }
    }

    public static int getMaxPlayersPerTeam() {
        return MAX_PLAYERS_PER_TEAM;
    }

    public static int getMinPlayersPerTeam() {
        return MIN_PLAYERS_PER_TEAM;
    }

    public static int getMatchTime() {
        return MATCH_TIME;
    }

    public static int getWinScore() {
        return WIN_SCORE;
    }

    public static int getFlagBaseRadius() {
        return FLAG_BASE_RADIUS;
    }

    public static boolean doesResetMatchAfterScore() {
        return RESET_MATCH_AFTER_SCORE;
    }

    public static List<ItemStack> getPlayerKit() {
        return PLAYER_KIT.stream().map(ItemStack::clone).collect(Collectors.toList());
    }

    public static boolean isStarterKitEnabled() {
        return STARTER_KIT_ENABLED;
    }

    public static Location getSpawn() {
        if (SPAWN == null) return Bukkit.getWorlds().getFirst().getSpawnLocation();
        return SPAWN.clone();
    }
}
