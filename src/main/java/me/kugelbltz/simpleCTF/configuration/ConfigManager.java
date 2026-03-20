package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.Bukkit;

public class ConfigManager {

    // --- Strings ---
    public static String PREFIX;
    public static String TEAM_JOIN;
    public static String TEAM_LEAVE;
    public static String PLAYER_JOINED_TEAM;
    public static String PLAYER_LEFT_TEAM;
    public static String MATCH_WIN;
    public static String NO_PERMISSION;

    // --- Match variables ---
    public static int MAX_PLAYERS_PER_TEAM;
    public static int MIN_PLAYERS_PER_TEAM;
    public static int MATCH_TIME;

    public ConfigManager() {
        init();
    }

    public static void init() {
        // As task, just in case init is triggered before complete startup.
        Bukkit.getScheduler().runTask(SimpleCTF.getInstance(), () -> {
            // --- Init: Strings ---
            PREFIX = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Prefix", "<#eb6434>SimpleCTF » <#e8cd33>");
            NO_PERMISSION = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.NoPermission", "<red>You don't have the permission to execute that command!");
            TEAM_JOIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.TeamJoin", "You joined the %color% team!");
            TEAM_LEAVE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.TeamLeave", "You left the team.");
            PLAYER_JOINED_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerJoinedTeam", "%player% joined the team!");
            PLAYER_LEFT_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerLeftTeam", "%player% left the team.");
            MATCH_WIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchWin");

            // --- Init: Match variables ---
            MAX_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MaxPlayersPerTeam");
            MIN_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MinPlayersPerTeam");
            MATCH_TIME = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.MatchTime");
        });

    }


}
