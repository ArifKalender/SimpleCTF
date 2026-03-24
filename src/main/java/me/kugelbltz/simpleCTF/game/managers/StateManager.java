package me.kugelbltz.simpleCTF.game.managers;

import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class StateManager {

    private static Set<Material> leatherArmors = new HashSet<>();

    static {
        leatherArmors.addAll(Set.of(
                Material.LEATHER_HELMET,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS
        ));
    }

    /**
     * Resets the given player's state for the following: Experience, Level, Food level, Health, Inventory, Potions
     */
    public void resetPlayerState(Player player, boolean giveKit, boolean clearInventory, @Nullable Team team) {
        if (clearInventory) player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        if (giveKit && team != null) addKitToInventory(player, team);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
    }

    private void addKitToInventory(Player player, Team team) {
        if (!StaticVariables.isStarterKitEnabled()) return;
        for (ItemStack itemStack : StaticVariables.getPlayerKit()) {
            if (itemStack.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(Team.getTeamRGB(team));
                itemStack.setItemMeta(armorMeta);
            }
            GeneralUtils.addItem(player, itemStack);
        }
    }
}
