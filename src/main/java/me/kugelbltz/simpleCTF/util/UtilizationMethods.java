package me.kugelbltz.simpleCTF.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UtilizationMethods {

    public static void addItem(Player player, ItemStack itemStack) {
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            return;
        }
        player.getInventory().addItem(itemStack);
    }

}
