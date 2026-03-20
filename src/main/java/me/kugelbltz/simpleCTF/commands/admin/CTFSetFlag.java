package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CTFSetFlag {
    // TODO: Implement
    public CTFSetFlag(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.setflag")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        String color = args[1].toUpperCase(Locale.ENGLISH);
        Location location = player.getLocation();
        location.setPitch(0);
        location.setYaw(0);
        location.setX((long) location.getX());
        location.setY((long) location.getY());
        location.setZ((long) location.getZ());
        switch (color) {
            case "RED" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.RedFlag", location);
                player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PREFIX + "Make sure to /ctf reload for the changes to take effect!"));
            }
            case "BLUE" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.BlueFlag", location);
                player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PREFIX + "Make sure to /ctf reload for the changes to take effect!"));
            }
            default -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid color! Valid colors: RED, BLUE"));

        }
    }
}
