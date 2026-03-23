package me.kugelbltz.simpleCTF.model;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public boolean isRedFlag(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        Component displayName = itemStack.getItemMeta().displayName();
        if (displayName == null) return false;
        return itemStack.getType() == Team.RED.getBannerItem() && displayName.equals(this.redFlag.getItemMeta().displayName());
    }

    public boolean isBlueFlag(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        Component displayName = itemStack.getItemMeta().displayName();
        if (displayName == null) return false;
        return itemStack.getType() == Team.BLUE.getBannerItem() && displayName.equals(this.blueFlag.getItemMeta().displayName());
    }

    public boolean isFlag(ItemStack itemStack) {
        return isRedFlag(itemStack) || isBlueFlag(itemStack);
    }

    public boolean isFlag(ItemStack itemStack, Team targetTeam) {
        if (targetTeam == Team.BLUE) return isBlueFlag(itemStack);
        else if (targetTeam == Team.RED) return isRedFlag(itemStack);
        else return false;
    }

    public ItemStack getBlueFlag() {
        return blueFlag.clone();
    }

    public ItemStack getRedFlag() {
        return redFlag.clone();
    }
}
