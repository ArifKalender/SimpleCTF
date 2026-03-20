package me.kugelbltz.simpleCTF.model;

import org.bukkit.entity.Player;

import java.util.List;

public class Match {

    public Match(List<Player> redTeam, List<Player> blueTeam) {
        // TODO: Implement
        redTeam.forEach(player -> {
            player.teleportAsync(MatchMap.getRedLocation());
        });
        blueTeam.forEach(player -> {
            player.teleportAsync(MatchMap.getBlueLocation());
        });

    }
}
