package me.kugelbltz.simpleCTF.model;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;
import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class BannerItems {
    private final ItemStack blueFlag = new ItemStack(Team.BLUE.getBannerItem(), 1);
    private final ItemStack redFlag = new ItemStack(Team.RED.getBannerItem(), 1);

    public BannerItems() {
        ItemMeta redMeta = redFlag.getItemMeta();
        redMeta.displayName(getMM().deserialize("<red>Red Flag"));
        ItemMeta blueMeta = blueFlag.getItemMeta();
        blueMeta.displayName(getMM().deserialize("<blue>Blue Flag"));

        redFlag.setItemMeta(redMeta);
        blueFlag.setItemMeta(blueMeta);
    }

    /** @return A copy of the flag item of the given team */
    public ItemStack  getFlag(Team team) {
        Team.requirePlayableTeam(team);
        if (team == Team.RED) return redFlag.clone();
        else if (team == Team.BLUE) return blueFlag.clone();
        else return null; // Unreachable
    }


    /** @return whether the given ItemStack belongs to any team */
    public boolean isFlag(ItemStack itemStack) {
        return isFlag(itemStack, Team.RED) || isFlag(itemStack, Team.BLUE);
    }

    /** @return whether the given ItemStack belongs to the given Team */
    public boolean isFlag(ItemStack itemStack, Team team) {
        if (itemStack.getItemMeta() == null) return false;
        Component displayName = itemStack.getItemMeta().displayName();
        if (displayName == null) return false;
        if (team == Team.BLUE && displayName.equals(this.blueFlag.displayName())) return true;
        if (team == Team.RED && displayName.equals(this.redFlag.displayName())) return true;
        else return false;
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
