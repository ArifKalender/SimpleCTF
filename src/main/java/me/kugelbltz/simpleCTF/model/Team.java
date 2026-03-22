package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.kugelbltz.simpleCTF.SimpleCTF.BANNER_ITEMS;

public enum Team {
    RED,
    BLUE,
    NONE;

    /**
     * @return The team of the given player
     */
    public static Team getTeam(Player player) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return NONE;
        if (match.getTeamPlayers(BLUE).contains(player)) return BLUE;
        if (match.getTeamPlayers(RED).contains(player)) return RED;
        return NONE;
    }

    /**
     * @return The enemy team of the given team
     */
    public static Team getOpposite(Team team) {
        if (team == RED) return BLUE;
        else if (team == BLUE) return RED;
        else return NONE;
    }

    /**
     * @return The {@code ItemStack} item of the given team, refer to {@code BannerItems}
     */
    public static ItemStack getTeamFlag(Team team) {
        if (team == RED) return BANNER_ITEMS.redFlag;
        else if (team == BLUE) return BANNER_ITEMS.blueFlag;
        else return null;
    }

    /**
     * @return The Team of the given {@code ItemStack}, if given item is invalid returns {@code Team.NONE}
     */
    public static Team getTeamFromFlag(ItemStack itemStack) {
        if (BANNER_ITEMS.isBlueFlag(itemStack)) return BLUE;
        else if (BANNER_ITEMS.isRedFlag(itemStack)) return RED;
        else return NONE;
    }
}
