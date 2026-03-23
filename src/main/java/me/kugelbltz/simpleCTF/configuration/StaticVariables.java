package me.kugelbltz.simpleCTF.configuration;

import me.kugelbltz.simpleCTF.SimpleCTF;

public class StaticVariables {

    private static int MAX_PLAYERS_PER_TEAM;
    private static int MIN_PLAYERS_PER_TEAM;
    private static int MATCH_TIME;
    private static int WIN_SCORE;
    private static int FLAG_BASE_RADIUS;

    public static void init() {
        MAX_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MaxPlayersPerTeam", 4);
        MIN_PLAYERS_PER_TEAM = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Queue.MinPlayersPerTeam", 1);
        MATCH_TIME = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.MatchTime", 600);
        WIN_SCORE = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.WinScore", 3);
        FLAG_BASE_RADIUS = SimpleCTF.getInstance().getConfig().getInt("SimpleCTF.Game.Match.FlagBaseRadius", 4);
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
}
