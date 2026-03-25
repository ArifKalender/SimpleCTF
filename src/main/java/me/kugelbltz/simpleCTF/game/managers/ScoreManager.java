package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.model.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {

    private final Map<Team, Integer> teamScores = new HashMap<>();

    public int getScore(Team team) {
        if (team == null) return 0;
        return teamScores.getOrDefault(team, 0);
    }

    public void setScore(Team team, int newScore) {
        if (team == null) return;
        teamScores.put(team, newScore);
    }

    public void resetScores() {
        for (Team team : Team.playableTeams()) {
            setScore(team, 0);
        }
    }

}
