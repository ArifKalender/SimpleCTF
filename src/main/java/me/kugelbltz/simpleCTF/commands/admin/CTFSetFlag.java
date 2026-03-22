package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class CTFSetFlag implements CTFCommand {

    /**
     * Command for setting a teams flag locations
     */
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.setflag")) {
            player.sendMessage(getMM().deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(getMM().deserialize("<red>Correct usage: /ctf setflag <red | blue>"));
            return;
        }
        String color = args[1].toUpperCase(Locale.ENGLISH);
        Location location = player.getLocation();
        location.setPitch(0);
        location.setX(Math.floor(location.getX()));
        location.setY(Math.floor(location.getY()));
        location.setZ(Math.floor(location.getZ()));
        switch (color) {
            case "RED" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.RedFlag", location);
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(getMM().deserialize(StaticVariables.PREFIX + "Set the location for the red flag!"));
            }
            case "BLUE" -> {
                SimpleCTF.getInstance().getConfig().set("Match.Locations.BlueFlag", location);
                SimpleCTF.getInstance().saveConfig();
                player.sendMessage(getMM().deserialize(StaticVariables.PREFIX + "Set the location for the blue flag!"));
            }
            default -> player.sendMessage(getMM().deserialize("<red>Invalid color! Valid colors: RED, BLUE"));
        }
    }
}
