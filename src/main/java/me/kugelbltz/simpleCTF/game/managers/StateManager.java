package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class StateManager {

    /**
     * Resets the given player's state for the following: Experience, Level, Food level, Health, Inventory, Potions
     */
    public void resetPlayerState(Player player, boolean giveKit, boolean clearInventory, Match match) {
        if (clearInventory) player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        if (giveKit && match != null) addKitToInventory(player, match);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
    }

    private void addKitToInventory(Player player, Match match) {
        if (!StaticVariables.isStarterKitEnabled()) return;
        for (ItemStack itemStack : StaticVariables.getPlayerKit()) {
            if (itemStack.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(match.getTeam(player).getTeamRGB());
                itemStack.setItemMeta(armorMeta);
            }
            GeneralUtils.addItem(player, itemStack);
        }
    }
}
