package me.kugelbltz.simpleCTF.model;

import org.bukkit.entity.Player;

import java.util.List;

public class Match {

    public Match(List<Player> redTeam, List<Player> blueTeam, String worldFileName) {
        MatchMap.getMatchMap(worldFileName).loadWorldForPlayers(blueTeam, redTeam);
        // TODO: Implement
    }
}
