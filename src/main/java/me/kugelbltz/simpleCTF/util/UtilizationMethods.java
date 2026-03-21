package me.kugelbltz.simpleCTF.util;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.kugelbltz.simpleCTF.SimpleCTF.BANNER_ITEMS;

public class UtilizationMethods {

    public static void addItem(Player player, ItemStack itemStack) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            return;
        }
        player.getInventory().addItem(itemStack);
    }

    public static void removeFlag(Player player, Team teamColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        ItemStack target = null;
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) continue;
            else if (teamColor == Team.RED && BANNER_ITEMS.isRedFlag(item)) target = item;
            else if (teamColor == Team.BLUE && BANNER_ITEMS.isBlueFlag(item)) target = item;
            else continue;
        }
        if (target == null) return;
        player.getInventory().removeItem(target);
        if (teamColor == Team.RED) match.setRedFlagCarrier(null);
        else match.setBlueFlagCarrier(null);
    }

}
