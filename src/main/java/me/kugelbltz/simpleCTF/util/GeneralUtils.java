package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.BannerItems;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;

public class GeneralUtils {

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
     *
     * @return true if a flag is found, false otherwise
     */
    public static boolean removeFlag(Player player, Team teamColor) {
        Match match = SimpleCTF.getCurrentMatch();
        if (match == null) return false;
        ItemStack target = null;
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (getBannerItems().isFlag(item, teamColor)) {
                target = item;
                break;
            }
        }
        if (target == null) return false;
        match.getFlagManager().setFlagCarrier(null, teamColor);
        player.getInventory().removeItem(target);
        return true;
    }

    /**
     * Drops all the flags from the player's inventory
     *
     * @return true if a flag is found, false otherwise
     */
    public static boolean dropAllFlags(Player player) {
        Match match = SimpleCTF.getCurrentMatch();
        if (match == null) return false;
        boolean found = false;
        List<ItemStack> toRemove = new ArrayList<>(); // Prevent ConcurrentModificationException
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            else if (getBannerItems().isFlag(item)) {
                toRemove.add(item);
                Item drop = player.getWorld().dropItem(player.getLocation(), item);
                Team flagTeam = BannerItems.getTeamFromFlag(item);
                match.getFlagManager().protectFlagItemEntity(drop);
                match.getFlagManager().setFlagCarrier(drop, flagTeam);
                match.getFlagManager().broadcastFlagDropLocation(flagTeam, player, player.getLocation());
                found = true;
            }
        }
        toRemove.forEach(itemStack -> player.getInventory().remove(itemStack));
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
