package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFSetFlag {

    /**
     * Command for setting a teams flag locations
     */
    public static void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.setflag")) {
            player.sendMessage(MM.deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(MM.deserialize("<red>Correct usage: /ctf setflag <red|blue>"));
            return;
        }
        String color = args[1].toUpperCase(Locale.ENGLISH);
        Location location = player.getLocation();
        location.setPitch(0);
        location.setX(Math.floor(location.getX()));
        location.setY((int) location.getY());
        location.setZ(Math.floor(location.getZ()));
        switch (color) {
            case "RED" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.RedFlag", location);
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(MM.deserialize(StaticVariables.PREFIX + "Set the location for the red flag!"));
            }
            case "BLUE" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.BlueFlag", location);
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(MM.deserialize(StaticVariables.PREFIX + "Set the location for the blue flag!"));
            }
            default -> player.sendMessage(MM.deserialize("<red>Invalid color! Valid colors: RED, BLUE"));
        }
    }
}
