package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

import static me.kugelbltz.simpleCTF.SimpleCTF.BANNER_ITEMS;

public class UtilizationMethods {

    /**
     * Safely adds or drops the given item to a player's inventory
     */
    public static void addItem(Player player, ItemStack itemStack) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            return;
        }
        player.getInventory().addItem(itemStack);
    }

    /**
     * Removes the given flag once from the player's inventory
     * @return true if a flag is found, false otherwise
     */
    public static boolean removeFlag(Player player, Team teamColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return false;
        ItemStack target = null;
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (BANNER_ITEMS.isFlag(item, teamColor)) {
                target = item;
                break;
            }
            else continue;
        }
        if (target == null) return false;
        player.getInventory().removeItem(target);
        match.setFlagCarrier(null, Team.getTeamFromFlag(target));
        return true;
    }

    /**
     * Drops all the flags from the player's inventory
     * @return true if a flag is found, false otherwise
     */
    public static boolean dropAllFlags(Player player) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return false;
        boolean found = false;
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            else if (BANNER_ITEMS.isFlag(item)) {
                player.getInventory().removeItem(item);
                Item drop = player.getWorld().dropItem(player.getLocation(), item);
                Team flagTeam = Team.getTeamFromFlag(item);
                match.setFlagCarrier(drop, flagTeam);
                match.broadcastFlagDropLocation(flagTeam, player, player.getLocation());
                found = true;
            }
            else continue;
        }
        return found;
    }

    /**
     * Plays the given sound for the given {@code Collection<Player>}
     */
    public static void playSoundForGroup(Collection<Player> players, Sound sound, @Nullable Float volume, @Nullable Float pitch) {
        if (volume == null) volume = 1f;
        if (pitch == null) pitch = 1f;
        for (Player player : players) {
            player.playSound(player, sound, volume, pitch);
        }
    }
}
