package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {

    private final Map<Team, Integer> teamScores = new HashMap<>();
    private final Match match;

    public ScoreManager(Match match) {
        this.match = match;
    }


    /**
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public int getScore(Team team) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        return teamScores.getOrDefault(team, 0);
    }

    /**
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void setScore(Team team, int newScore) {
        if (team == Team.NONE) throw new IllegalArgumentException("Team NONE is not allowed");
        teamScores.put(team, newScore);
    }

}
