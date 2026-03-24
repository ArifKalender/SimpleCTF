package me.kugelbltz.simpleCTF.model;

import me.kugelbltz.simpleCTF.SimpleCTF;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;
import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class BannerItems {
    private final ItemStack blueFlag = new ItemStack(Team.BLUE.getBannerItem(), 1);
    private final ItemStack redFlag = new ItemStack(Team.RED.getBannerItem(), 1);
    private final static Map<Team, ItemStack> teamFlags = new HashMap<>();

    NamespacedKey key = new NamespacedKey(SimpleCTF.getInstance(), "team_flag");

    public BannerItems() {
        ItemMeta redMeta = redFlag.getItemMeta();
        redMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "RED");
        redMeta.displayName(getMM().deserialize("<red>Red Flag"));
        ItemMeta blueMeta = blueFlag.getItemMeta();
        blueMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "BLUE");
        blueMeta.displayName(getMM().deserialize("<blue>Blue Flag"));

        redFlag.setItemMeta(redMeta);
        blueFlag.setItemMeta(blueMeta);
        teamFlags.put(Team.RED, redFlag);
        teamFlags.put(Team.BLUE, blueFlag);
    }

    /** @return A copy of the flag item of the given team */
    public ItemStack  getFlag(Team team) {
        Team.requirePlayableTeam(team);
        return teamFlags.get(team);
    }


    /** @return whether the given ItemStack belongs to any team */
    public boolean isFlag(ItemStack itemStack) {
        return itemStack.getPersistentDataContainer().has(key);
    }

    /** @return whether the given ItemStack belongs to the given Team */
    public boolean isFlag(ItemStack itemStack, Team team) {
        if (itemStack.getItemMeta() == null) return false;
        String itemTeam = itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return team.name().equalsIgnoreCase(itemTeam);
    }

    /**
     * @return The Team of the given {@code ItemStack}, if given item is invalid returns {@code Team.NONE}
     */
    public static Team getTeamFromFlag(ItemStack itemStack) {
        if (getBannerItems().isFlag(itemStack, Team.BLUE)) return Team.BLUE;
        else if (getBannerItems().isFlag(itemStack, Team.RED)) return Team.RED;
        else return Team.NONE;
    }
}
