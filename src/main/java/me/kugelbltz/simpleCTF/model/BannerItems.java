package me.kugelbltz.simpleCTF.model;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BannerItems {
    public final ItemStack blueFlag = new ItemStack(Material.BLUE_BANNER, 1);
    public final ItemStack redFlag = new ItemStack(Material.RED_BANNER, 1);

    public BannerItems() {
        ItemMeta redMeta = redFlag.getItemMeta();
        redMeta.setDisplayName("§cRed Flag");
        ItemMeta blueMeta = blueFlag.getItemMeta();
        blueMeta.setDisplayName("§9Blue Flag");

        redFlag.setItemMeta(redMeta);
        blueFlag.setItemMeta(blueMeta);
    }
}
