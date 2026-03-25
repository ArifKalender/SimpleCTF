package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class CTFSetFlag implements CTFCommand {

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize("<red>Correct usage: /ctf setflag <red | blue>"));
            return;
        }

        Team team;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize("<red>Invalid color! Valid colors: RED, BLUE"));
            return;
        }

        // Sets config value for the team
        SimpleCTF.getInstance().getConfig().set("Match.Locations." + team.name().toUpperCase(Locale.ENGLISH), prepareLocation(player.getLocation()));
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "Set the location for " + team.name().toUpperCase(Locale.ENGLISH) + " flag!"));
        SimpleCTF.getInstance().saveConfig();
    }

    @Override
    public List<String> getArguments() {
        return List.of("red", "blue");
    }

    @Override
    public String getPermission() {
        return "simplectf.admin.setflag";
    }

    private Location prepareLocation(Location playerLocation) {
        Location toReturn = playerLocation.clone();
        toReturn.setPitch(0);
        toReturn.setX(Math.floor(toReturn.getX())+0.5);
        toReturn.setY(Math.floor(toReturn.getY()));
        toReturn.setZ(Math.floor(toReturn.getZ())+0.5);
        return toReturn;
    }

}
