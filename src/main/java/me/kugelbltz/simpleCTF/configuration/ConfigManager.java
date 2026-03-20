package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.Bukkit;

public class ConfigManager {

    // --- Strings ---
    public static String PREFIX;
    public static String NO_PERMISSION;
    public static String NO_FRIENDLY_FIRE;
    public static String WRONG_BANNER_TEAM;
    public static String TEAM_JOIN;
    public static String TEAM_LEAVE;
    public static String PLAYER_JOINED_TEAM;
    public static String PLAYER_LEFT_TEAM;
    public static String MATCH_WIN;
    public static String MATCH_TIME_OUT;
    public static String MATCH_START;
    public static String PLAYER_CAUGHT_FLAG;
    public static String PLAYER_PLACE_FLAG;

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
            NO_PERMISSION = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.NoPermission", "<red>You don't have the permission to execute that command!");
            NO_FRIENDLY_FIRE = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.NoFriendlyFire", PREFIX + "You can not attack your teammates!");
            WRONG_BANNER_TEAM = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.WrongBannerTeam", PREFIX + "Only %color% team's players can pickup this banner!");
            TEAM_JOIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.TeamJoin", PREFIX + "You joined the %color% team!");
            TEAM_LEAVE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.TeamLeave", PREFIX + "You left the team.");
            PLAYER_JOINED_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.PlayerJoinedTeam", PREFIX + "%player% joined the team!");
            PLAYER_LEFT_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.PlayerLeftTeam", PREFIX + "%player% left the team.");
            MATCH_WIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchWin");
            MATCH_TIME_OUT = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchTimeOut", PREFIX + "The match ran out of time!");
            MATCH_START = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchStart", PREFIX + "The match just started!");
            PLAYER_CAUGHT_FLAG = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerCaughtFlag", PREFIX + "%player% CAUGHT THE %color% FLAG!");
            PLAYER_PLACE_FLAG = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerPlaceFlag", PREFIX + "%player% placed back their flag!");

            // --- Init: Match variables ---
            MAX_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MaxPlayersPerTeam", 4);
            MIN_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MinPlayersPerTeam", 1);
            MATCH_TIME = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.MatchTime", 600);
        });

    }


}
