package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.model.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {

    private final Map<Team, Integer> teamScores = new HashMap<>();

    /**
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public int getScore(Team team) {
        Team.requirePlayableTeam(team);
        return teamScores.getOrDefault(team, 0);
    }

    /**
     * @throws IllegalArgumentException if team is {@link Team#NONE}
     */
    public void setScore(Team team, int newScore) {
        Team.requirePlayableTeam(team);
        teamScores.put(team, newScore);
    }

}
