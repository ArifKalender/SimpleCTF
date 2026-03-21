package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFStop {
    public CTFStop(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(MM.deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (!isMatchRunning) {
            player.sendMessage(MM.deserialize("<red>There is no match going on."));
            return;
        }
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch( ConfigManager.PREFIX + "<red>Match interrupted by an admin!");
    }
}
