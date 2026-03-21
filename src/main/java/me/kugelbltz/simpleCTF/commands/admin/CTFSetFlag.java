package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFSetFlag {
    public CTFSetFlag(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.setflag")) {
            player.sendMessage(MM.deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(MM.deserialize(ConfigManager.INCORRECT_SYNTAX));
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
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(MM.deserialize(ConfigManager.PREFIX + "Make sure to /ctf reload for the changes to take effect!"));
            }
            case "BLUE" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.BlueFlag", location);
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(MM.deserialize(ConfigManager.PREFIX + "Make sure to /ctf reload for the changes to take effect!"));
            }
            default -> player.sendMessage(MM.deserialize("<red>Invalid color! Valid colors: RED, BLUE"));

        }
    }
}
