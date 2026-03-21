package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.entity.Player;

public enum Team {

    RED,
    BLUE,
    NONE;

    public static Team getTeam(Player player) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match==null) return NONE;
        if (match.getBluePlayers().contains(player)) return BLUE;
        if (match.getRedPlayers().contains(player)) return RED;
        return NONE;
    }

    public static Team getOpposite(Team team) {
        if (team.equals(RED)) return BLUE;
        else if (team.equals(BLUE)) return RED;
        else return NONE;
    }
}
