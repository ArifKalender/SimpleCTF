package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;

public class StaticVariables {

    // --- Strings ---
    public static String PREFIX;
    public static String NO_PERMISSION;
    public static String NO_FRIENDLY_FIRE;
    public static String WRONG_BANNER_TEAM;
    public static String INCORRECT_SYNTAX;

    public static String TEAM_JOIN;
    public static String TEAM_LEAVE;
    public static String PLAYER_JOINED_TEAM;
    public static String PLAYER_LEFT_TEAM;
    public static String ALREADY_IN_QUEUE;
    public static String TEAM_ALREADY_FULL;
    public static String NOT_IN_TEAM;

    public static String MATCH_WIN;
    public static String MATCH_TIME_OUT;
    public static String MATCH_START;
    public static String PLAYER_CAUGHT_FLAG;
    public static String PLAYER_RETURN_FLAG;
    public static String PLAYER_PLACE_FLAG;
    public static String MATCH_OCCUPIED;
    public static String CURRENT_SCORE;
    public static String FLAG_DROPPED_AT;

    // --- Match variables ---
    public static int MAX_PLAYERS_PER_TEAM;
    public static int MIN_PLAYERS_PER_TEAM;
    public static int MATCH_TIME;
    public static int WIN_SCORE;
    

    public static void init() {
        // --- Init: Strings ---
        PREFIX = SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Prefix", "<#eb6434>SimpleCTF » <#e8cd33>");
        NO_PERMISSION = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.NoPermission", "<red>You don't have the permission to execute that command!");
        NO_FRIENDLY_FIRE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.NoFriendlyFire", "You can not attack your teammates!");
        INCORRECT_SYNTAX = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.IncorrectSyntax", "That's an invalid command, are you sure you typed that correctly?");

        WRONG_BANNER_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Warnings.WrongBannerTeam", "You cannot break that flag!");
        TEAM_JOIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.TeamJoin", "You joined the %color% team!");
        TEAM_LEAVE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.TeamLeave", "You left the team.");
        PLAYER_JOINED_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.PlayerJoinedTeam", "%player% joined the team!");
        PLAYER_LEFT_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.PlayerLeftTeam", "%player% left the team.");
        ALREADY_IN_QUEUE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.AlreadyInQueue", "You are already in a queue! Use /ctf leave");
        TEAM_ALREADY_FULL = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.TeamAlreadyFull", "Sorry, but that team is full!");
        NOT_IN_TEAM = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Queue.NotInTeam", "You are not in a team.");

        MATCH_WIN = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchWin", "The %color% team won the match!");
        MATCH_TIME_OUT = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchTimeOut", "The match ran out of time!");
        MATCH_START = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchStart", "<bold>The match just started!");
        PLAYER_CAUGHT_FLAG = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerCaughtFlag", "%player% CAUGHT THE %color% FLAG!");
        PLAYER_RETURN_FLAG = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerReturnFlag", "%player% returned with %opposite_color% team's flag!");
        PLAYER_PLACE_FLAG = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.PlayerPlaceFlag", "%player% placed back their flag!");
        MATCH_OCCUPIED = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.MatchOccupied", "There already is a match going on in this server, sorry!");
        CURRENT_SCORE = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.CurrentScore", "Blue score: %blue_score% | Red score: %red_score%");
        FLAG_DROPPED_AT = PREFIX + SimpleCTF.getInstance().getConfig().getString("SimpleCTF.Strings.Match.FlagDroppedAt", "%player% dropped %color% flag at %coordinates%!");

        // --- Init: Match variables ---
        MAX_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MaxPlayersPerTeam", 4);
        MIN_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MinPlayersPerTeam", 1);
        MATCH_TIME = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.MatchTime", 600);
        WIN_SCORE = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.WinScore", 3);
    }

}
